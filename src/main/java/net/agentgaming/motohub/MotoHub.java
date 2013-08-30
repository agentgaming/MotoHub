package net.agentgaming.motohub;

import com.mike724.motoapi.portals.PortalManager;
import com.mike724.motoserver.MotoServer;
import net.agentgaming.motohub.effects.PotionSetter;
import net.agentgaming.motohub.events.HubListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class MotoHub extends JavaPlugin {

    private static MotoHub instance;

    //Portals
    public int tjPortalID;

    @Override
    public void onEnable() {
        //Set our instance
        instance = this;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeDay(), 1l, 1l);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PotionSetter(), 1l, 20l);

        this.getServer().getPluginManager().registerEvents(new HubListener(), this);

        setupPortals();

        this.getLogger().info("MotoHub Enabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("MotoHub Disabled");
    }

    public void setupPortals() {
        World w = Bukkit.getWorlds().get(0);
        Location tjPortal = new Location(w, 0, 62, 33);

        PortalManager pman = MotoServer.getInstance().getPortalManager();
        tjPortalID = pman.registerPortal(tjPortal.getBlock());
    }

    @SuppressWarnings("unused")
    public static MotoHub getInstance() {
        return instance;
    }
}