package net.agentgaming.motohub;

import com.mike724.motoapi.MotoAPI;import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeecordHelper {
    private MotoAPI plugin;

    private byte[] result = null;
    private boolean registered = false;

    public BungeecordHelper() {
        plugin = MotoAPI.getInstance();
    }

    private byte[] sendBungeMessageResult(final Player p, String cmd, String... args) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        String hash = cmd;

        try {
            out.writeUTF(cmd);
            for(String s : args) {
                out.writeUTF(s);
            }
        } catch (IOException e) { }

        result = null;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
                if(s == "BungeeCord" && player == p) {
                    result = bytes;
                }
            }
        });

        registered = true;

        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        while(result == null) { }

        return result;
    }

    private void sendBungeMessage(final Player p, String cmd, String... args) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        String hash = cmd;

        try {
            out.writeUTF(cmd);
            for(String s : args) {
                out.writeUTF(s);
            }
        } catch (IOException e) { }

        if(!registered) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
                }
            });
        }

        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    public void connectToServer(Player p, String server) {
        sendBungeMessage(p, "Connect", server);
    }

    @Deprecated //Depreacted to discourage use
    protected byte[] getServer() {
        return sendBungeMessageResult(Bukkit.getServer().getOnlinePlayers()[0], "GetServer");
    }

}
