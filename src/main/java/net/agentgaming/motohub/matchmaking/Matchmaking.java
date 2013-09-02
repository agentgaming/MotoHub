package net.agentgaming.motohub.matchmaking;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.mike724.motoapi.push.MotoPush;
import com.mike724.motoapi.push.ServerState;
import com.mike724.motoapi.push.ServerType;
import com.mike724.motoapi.storage.defaults.NetworkPlayer;
import com.mike724.motoserver.MotoServer;
import net.agentgaming.motohub.MotoHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class Matchmaking {
    private ServerType serverType;
    private Location waitingRoom;
    private Integer maxPlayers;

    private MotoPush mp;

    private ArrayList<Player> matchMaking;

    public Matchmaking(ServerType serverType, Integer maxPlayers, Location waitingRoom) {
        this.serverType = serverType;
        this.maxPlayers = maxPlayers;
        this.waitingRoom = waitingRoom;
        this.matchMaking = new ArrayList<Player>();

        mp = MotoServer.getInstance().getMotoPush();
    }

    public void findBestGame(final Player p) {
        if (isMatchmaking(p)) return;
        matchMaking.add(p);

        NetworkPlayer np = MotoServer.getInstance().getStorage().getObject(p.getName(), NetworkPlayer.class);
        JSONObject peers = mp.apiMethod("getpeersbytype", serverType.name());
        Iterator<?> keys = peers.keys();

        HashMap<String, Integer> canidates = new HashMap<>();

        Integer totalPeers = 0;
        Integer amountFull = 0;
        while (keys.hasNext()) {
            String key = (String) keys.next();
            totalPeers++;
            try {
                if (peers.get(key) instanceof JSONObject) {
                    JSONObject peer = (JSONObject) peers.get(key);
                    if (peer.getString("state") != ServerState.OPEN.name()) continue;
                    if (peer.getInt("numPlayers") >= maxPlayers) {
                        amountFull++;
                        continue;
                    }
                    if (peer.getString("alias") == "__unknown__") continue;

                    Integer numFriends = 0;
                    JSONArray players = peer.getJSONArray("players");

                    for (int i = 0; i < players.length(); i++) {
                        if (np.getFriends().contains(players.getString(i))) numFriends++;
                    }

                    canidates.put(peer.getString("alias"), numFriends);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        if (canidates.size() > 0) {
            TreeMap<String, Integer> sort = new TreeMap<>(new ValueComparator(canidates));
            sort.putAll(canidates);
            p.sendMessage(ChatColor.AQUA + "Found game on '" + sort.firstEntry().getKey() + "'!");
            connectToServer(p, sort.firstEntry().getKey());
        } else {
            if (totalPeers == 0) {
                p.sendMessage(ChatColor.RED + "This gamemode has no servers!");
            }
            if (totalPeers == amountFull) {
                p.sendMessage(ChatColor.AQUA + "All games are full.. we will keep trying.");
                p.teleport(waitingRoom);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MotoHub.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        findBestGame(p);
                    }
                }, 100);
            }
        }
    }

    public Boolean isMatchmaking(Player p) {
        return matchMaking.contains(p);
    }

    public void connectToServer(Player p, String s) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(s);
        } catch (IOException e) {
        }

        MotoHub.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(MotoHub.getInstance(), "BungeeCord");
        p.sendPluginMessage(MotoHub.getInstance(), "BungeeCord", b.toByteArray());
    }

    private class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
