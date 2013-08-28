package net.agentgaming.motohub;

import org.bukkit.plugin.java.JavaPlugin;

public class MotoHub extends JavaPlugin {

    private static MotoHub instance;

    @Override
    public void onEnable() {
        //Set our instance
        instance = this;

        this.getLogger().info("MotoHub Enabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("MotoHub Disabled");
    }

    public static MotoHub getInstance() {
        return instance;
    }
}