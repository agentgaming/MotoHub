package net.agentgaming.motohub;

import net.agentgaming.motohub.effects.PotionSetter;
import net.agentgaming.motohub.events.HubListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MotoHub extends JavaPlugin {

    private static MotoHub instance;

    @Override
    public void onEnable() {
        //Set our instance
        instance = this;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeDay(), 1l, 1l);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PotionSetter(), 1l, 20l);

        this.getServer().getPluginManager().registerEvents(new HubListener(), this);

        this.getLogger().info("MotoHub Enabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("MotoHub Disabled");
    }

    @SuppressWarnings("unused")
    public static MotoHub getInstance() {
        return instance;
    }
}