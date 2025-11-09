package de.chaosfamilie.minecraft.tpa.commands;

import de.chaosfamilie.minecraft.tpa.TpRequest;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.chaosfamilie.minecraft.tpa.TpaPlugin.is_proxy;
import static de.chaosfamilie.minecraft.tpa.TpaPlugin.requests;

public class TpaCommand implements BasicCommand {
    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(@NotNull CommandSourceStack sourceStack, String @NotNull [] args) {
        if (!(sourceStack.getSender() instanceof Player player)) {
            sourceStack.getSender().sendMessage(mm.deserialize("<red>You have to be a player to ues this command!"));
            return;
        }

        if (args.length >= 2) {
            player.sendMessage(mm.deserialize("<red>Too many arguments"));
            return;
        } else if (args.length == 0) {
            player.sendMessage(mm.deserialize("<red>/tpa <player>"));
            return;
        }

        if (is_proxy) {
            // TODO
        } else {
            var target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(mm.deserialize("<red>Player not found"));
                return;
            }

            target.sendMessage(mm.deserialize("<gold>" + player.getName() + "<green> wants to teleport to you"));
            target.sendMessage(mm.deserialize("<green>You have <gold><bold>5 Minutes</bold><green> to accept"));
            target.sendMessage(mm.deserialize("<green><click:run_command:'/tpaaccept " + player.getName() + "'>[ACCEPT]</click><dark_gray> | <red><click:run_command:'/tpadecline " + player.getName() + "'>[DECLINE]"));

            target.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 1));

            requests.add(new TpRequest(player.getUniqueId(), target.getUniqueId(), Instant.now().getEpochSecond(), target.getLocation(), ""));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack sourceStack, String @NotNull [] args) {
        if (args.length >= 1) {
            return List.of();
        }

        var list = new ArrayList<String>();

        if (is_proxy) {
            // TODO
        } else {
            for (var pp : Bukkit.getOnlinePlayers()) {
                list.add(pp.getName());
            }
        }

        return list;
    }
}
