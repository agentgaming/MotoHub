package net.agentgaming.motohub.matchmaking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.util.*;

public class Matchmaking {
    private ServerType serverType;
    private Location waitingRoom;
    private Integer maxPlayers;

    private MotoPush mp;
    private Gson gson;

    private ArrayList<Player> matchMaking;
    private HashMap<Player, ArrayList<Player>> groups;

    public Matchmaking(ServerType serverType, Integer maxPlayers, Location waitingRoom) {
        this.serverType = serverType;
        this.maxPlayers = maxPlayers;
        this.waitingRoom = waitingRoom;
        this.matchMaking = new ArrayList<Player>();
        this.groups = new HashMap<Player, ArrayList<Player>>();

        mp = MotoServer.getInstance().getMotoPush();
        gson = new Gson();
    }

    public void findBestGame(final Player... p) {
        for (Player player : p) {
            if (isMatchmaking(player))
                matchMaking.add(player);
        }

        String json = mp.apiMethod("getpeersbytype", serverType.name()).toString();
        Type type = new TypeToken<ArrayList<PeerByType>>() {
        }.getType();
        ArrayList<PeerByType> peers = gson.fromJson(json, type);

        HashMap<String, Integer> candidates = new HashMap<>();
        Integer totalPeers = peers.size();
        Integer amountFull = 0;

        for (PeerByType peer : peers) {
            if (peer.getNumPlayers() + p.length > maxPlayers || !peer.getState().equals(ServerState.OPEN) || peer.getAlias().equals("__unknown__")) {
                amountFull++;
                continue;
            }

            if (p.length == 1) {
                NetworkPlayer np = MotoServer.getInstance().getStorage().getObject(p[0].getName(), NetworkPlayer.class);

                Integer numFriends = 0;
                for (String s : peer.getPlayers()) if (np.getFriends().contains(s)) numFriends++;

                candidates.put(peer.getAlias(), numFriends);
            } else if (p.length > 1) {
                candidates.put(peer.getAlias(), 0);
                break;
            }
        }

        if (candidates.size() > 0) {
            TreeMap<String, Integer> sort = new TreeMap<>(new ValueComparator(candidates));
            sort.putAll(candidates);

            for (Player player : p) {
                player.sendMessage(ChatColor.AQUA + "Found game on '" + sort.firstEntry().getKey() + "'!");
                matchMaking.remove(p);
                connectToServer(player, sort.firstEntry().getKey());
            }
        } else {
            if (totalPeers == 0) {
                for (Player player : p) {
                    player.sendMessage(ChatColor.RED + "This gamemode has no servers!");
                    matchMaking.remove(p);
                }
            } else if (totalPeers == amountFull) {
                for (Player player : p) {
                    player.sendMessage(ChatColor.AQUA + "All games are full.. we will keep trying.");
                    player.teleport(waitingRoom);
                }
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

    public void joinGroup(Player leader, Player member) {
        if (isInGroup(member)) {
            member.sendMessage(ChatColor.RED + "You are already in a group!");
            return;
        }

        boolean cancel = false;

        if (isMatchmaking(leader)) {
            member.sendMessage(ChatColor.AQUA + "You cannot join this group because the leader is matchmaking");
            leader.sendMessage(ChatColor.AQUA + "'" + member.getDisplayName() + "' could not join your group because you are matchmaking");
            cancel = true;
        }

        if (isMatchmaking(member)) {
            member.sendMessage(ChatColor.AQUA + "You can't join a group while you are matchmaking.");
            cancel = true;
        }

        if (cancel) return;
        if (!groups.containsKey(leader)) return;

        groups.get(leader).add(member);
        member.sendMessage(ChatColor.AQUA + "You have joined '" + leader.getDisplayName() + "'s group!");
    }

    public void leaveGroup(Player leader, Player member) {
        if (leader == member) {
            for (Player p : groups.get(leader)) {
                p.sendMessage(ChatColor.AQUA + "Your group has been disbanded!");
            }
            groups.remove(leader);
            return;
        }

        if (groups.get(leader).contains(member)) {
            groups.get(leader).remove(member);
            member.sendMessage(ChatColor.AQUA + "You have left '" + leader.getDisplayName() + "'s group!");
        } else {
            member.sendMessage(ChatColor.RED + "You are not in '" + leader.getDisplayName() + "'s group!");
        }
    }

    public boolean isInGroup(Player p) {
        if (groups.containsKey(p)) return true;
        for (Player k : groups.keySet()) {
            if (groups.get(k).contains(p)) return true;
        }
        return false;
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
