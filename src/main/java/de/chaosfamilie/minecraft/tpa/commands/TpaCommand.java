package de.chaosfamilie.minecraft.tpa.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
import org.jspecify.annotations.NullMarked;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

@NullMarked
public class TpaCommand {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static LiteralCommandNode<CommandSourceStack> constructCommand() {
        return Commands.literal("tpa")
                .requires(ctx -> ctx.getExecutor() instanceof Player)
                .then((Commands
                        .argument("player", StringArgumentType.string()))
                        .suggests(NetworkPlayerSuggest::suggestPlayers)
                        .executes(TpaCommand::execute))
                .build();
    }

    private static int execute(final CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getExecutor();
        var target_name = ctx.getArgument("player", String.class);
        var target = Bukkit.getPlayer(target_name);
        var isLocal = target != null && TpaPlugin.is_proxy;

        if (TpaPlugin.is_proxy && !isLocal) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF(target_name);
            out.writeUTF("tpa_request");

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);

            try {
                assert player != null;
                msgout.writeUTF(player.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            player.sendPluginMessage(TpaPlugin.plugin, "BungeeCord", out.toByteArray());
        } else {
            if (target == null) {
                assert player != null;
                player.sendMessage(mm.deserialize("<red>Player not found"));
                return 0;
            }

            assert player != null;
            target.sendMessage(mm.deserialize("<gold>" + player.getName() + "<green> wants to teleport to you"));
            target.sendMessage(mm.deserialize("<green>You have <gold><bold>5 Minutes</bold><green> to accept"));
            target.sendMessage(mm.deserialize("<green><click:run_command:'/tpaaccept " + player.getName() + "'>[ACCEPT]</click><dark_gray> | <red><click:run_command:'/tpadecline " + player.getName() + "'>[DECLINE]"));

            target.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));

            TpaPlugin.requests.add(new TpRequest(player.getUniqueId(), target.getUniqueId(), Instant.now().getEpochSecond(), target.getLocation(), ""));
        }

        return 1;
    }
}
