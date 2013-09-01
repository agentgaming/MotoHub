package net.agentgaming.motohub;

import com.mike724.motoapi.midi.MidiBroadcaster;
import com.mike724.motoapi.portals.PortalManager;
import com.mike724.motoapi.push.MotoPush;
import com.mike724.motoapi.push.ServerState;
import com.mike724.motoapi.push.ServerType;
import com.mike724.motoserver.MotoCommands;
import com.mike724.motoserver.MotoServer;
import net.agentgaming.motohub.effects.PotionSetter;
import net.agentgaming.motohub.events.HubListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sound.midi.MidiUnavailableException;

public class MotoHub extends JavaPlugin {

    private static MotoHub instance;

    //Portals
    public int tjPortalID;

    //World spawn
    Location worldSpawn;

    //MidiBroadcaster
    MidiBroadcaster midi;

    @Override
    public void onEnable() {
        //Set our instance
        instance = this;

        //Setup sync tasks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeDay(), 1l, 1l);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PotionSetter(), 1l, 20l);

        //Register events
        this.getServer().getPluginManager().registerEvents(new HubListener(), this);

        //Setup portals
        setupPortals();

        //Setup spawn command
        this.worldSpawn = new Location(Bukkit.getWorlds().get(0), 0.5F, 65, 0.5F, 0, 0);
        getCommand("spawn").setExecutor(new HubCommands(this));

        //TODO: Temporary
        try {
            midi = new MidiBroadcaster();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        //Register the server with MotoPush
        MotoPush mp = MotoServer.getInstance().getMotoPush();
        mp.setType(ServerType.HUB);
        mp.setState(ServerState.OPEN);

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

    public Location getWorldSpawn() {
        return worldSpawn;
    }

    public MidiBroadcaster getMidi() {
        return midi;
    }

    @SuppressWarnings("unused")
    public static MotoHub getInstance() {
        return instance;
    }
}