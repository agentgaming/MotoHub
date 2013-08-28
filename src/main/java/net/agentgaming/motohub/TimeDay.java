package net.agentgaming.motohub;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeDay implements Runnable {

    @Override
    public void run() {
        for(World w : Bukkit.getWorlds()) {
            w.setTime(1500);
        }
    }

}
