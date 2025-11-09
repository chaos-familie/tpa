package de.chaosfamilie.minecraft.tpa.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.chaosfamilie.minecraft.tpa.NetworkPlayerSuggest;
import de.chaosfamilie.minecraft.tpa.TpRequest;
import de.chaosfamilie.minecraft.tpa.TpaPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;

import static de.chaosfamilie.minecraft.tpa.TpaPlugin.is_proxy;
import static de.chaosfamilie.minecraft.tpa.TpaPlugin.requests;

public class TpaAcceptCommand {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static LiteralCommandNode<CommandSourceStack> constructCommand() {
        return Commands.literal("tpaaccept")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then((Commands
                        .argument("player", StringArgumentType.string()))
                        .suggests(NetworkPlayerSuggest::suggestPlayers)
                        .executes(TpaAcceptCommand::execute))
                .build();
    }

    private static int execute(final CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getExecutor();
        var requester_name = ctx.getArgument("player", String.class);
        var requester = Bukkit.getPlayer(requester_name);
        var isLocal = requester != null && TpaPlugin.is_proxy;
        TpRequest request = null;

        for (var req : requests) {
            assert player != null;
            if (req.target() != player.getUniqueId()) continue;
            request = req;

            break;
        }

        if (request == null || request.created() + (60 * 5) <= Instant.now().getEpochSecond()) {
            assert player != null;
            player.sendMessage(mm.deserialize("<red>There is no teleport request from this player"));
            return 0;
        }

        assert requester != null;
        requester.sendMessage(mm.deserialize("<gold>" + player.getName() + "<green> Accepted your teleport request"));
        requester.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 2));

        if (is_proxy && !isLocal) {
            // TODO
        } else {
            requester.teleport(request.location());
        }

        requests.remove(request);
        return 1;
    }
}
