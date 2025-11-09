package de.chaosfamilie.minecraft.tpa.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class TpaCommand implements BasicCommand {
private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(@NotNull CommandSourceStack sourceStack, String @NotNull [] strings) {
        if(!(sourceStack.getSender() instanceof Player player)) {
            sourceStack.getSender().sendMessage(mm.deserialize("<red>You have to be a player to ues this command!"));
            return;
        }


    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack sourceStack, String @NotNull [] args) {
        if(!(sourceStack.getSender() instanceof Player player)) {
            return List.of();
        }

        

        return List.of();
    }
}
