package de.chaosfamilie.minecraft.tpa.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.chaosfamilie.minecraft.tpa.TpaPlugin.is_proxy;

public class TpaDeclineCommand implements BasicCommand {
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
            player.sendMessage(mm.deserialize("<red>/tpadecline <player>"));
            return;
        }

        if (is_proxy) {
            // TODO
        } else {
            var sender = Bukkit.getPlayer(args[0]);

            assert sender != null;
            sender.sendMessage(mm.deserialize("<gold>" + player.getName() + "<red> Declined your teleport request"));
            sender.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 0));
        }
    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack sourceStack, String @NotNull [] args) {
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
