package net.agentgaming.motohub.matchmaking;

import com.mike724.motoapi.push.ServerState;

import java.util.ArrayList;

class PeerByType {
    private String pushIP;
    private String mcIP;
    private String alias;
    private ServerState state;
    private int numPlayers;
    private ArrayList<String> players;

    public PeerByType() {
        players = new ArrayList<String>();
    }

    public String getPushIP() {
        return pushIP;
    }

    public String getMcIP() {
        return mcIP;
    }

    public String getAlias() {
        return alias;
    }

    public ServerState getState() {
        return state;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }
}
