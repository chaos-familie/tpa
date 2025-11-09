package de.chaosfamilie.minecraft.tpa;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class NetworkPlayerSuggest {
    public static CompletableFuture<Suggestions> suggestPlayers(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            return builder.buildFuture();
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        if (!TpaPlugin.is_proxy_tested) {
            out.writeUTF("GetServer");
            player.sendPluginMessage(TpaPlugin.plugin, "BungeeCord", out.toByteArray());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (TpaPlugin.is_proxy) {
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            player.sendPluginMessage(TpaPlugin.plugin, "BungeeCord", out.toByteArray());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (var entry : TpaPlugin.network_players) {
                builder.suggest(entry);
            }
        } else {
            for (var entry : Bukkit.getOnlinePlayers()) {
                builder.suggest(entry.getName());
            }
        }

        return builder.buildFuture();
    }
}
