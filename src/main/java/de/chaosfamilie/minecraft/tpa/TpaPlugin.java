package de.chaosfamilie.minecraft.tpa;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.chaosfamilie.minecraft.tpa.commands.TpaAcceptCommand;
import de.chaosfamilie.minecraft.tpa.commands.TpaCommand;
import de.chaosfamilie.minecraft.tpa.commands.TpaDeclineCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.Arrays;

public final class TpaPlugin extends JavaPlugin implements PluginMessageListener {
    public static Plugin plugin;
    public static boolean is_proxy = false;
    public static boolean is_proxy_tested = false;

    public static ArrayList<String> network_players = new ArrayList<>();
    public static ArrayList<TpRequest> requests = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(TpaCommand.constructCommand());
            commands.registrar().register(TpaAcceptCommand.constructCommand());
            commands.registrar().register(TpaDeclineCommand.constructCommand());
        });
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        var subchannel = in.readUTF();

        switch (subchannel) {
            case "GetServer" -> {
                is_proxy = true;
                is_proxy_tested = true;
            }
            case "PlayerList" -> {
                in.readUTF();

                network_players.clear();
                network_players.addAll(Arrays.asList(in.readUTF().split(", ")));
            }
            case "tpa_request" -> {
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());
            }
        }
    }
}
