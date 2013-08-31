package net.agentgaming.motohub.events;

import com.mike724.motoapi.portals.PortalEnterEvent;
import net.agentgaming.motohub.MotoHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

@SuppressWarnings("unused")
public class HubListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPortalEnter(PortalEnterEvent event) {
        Player p = event.getPlayer();
        Integer id = event.getPortal();
        Bukkit.broadcastMessage("Player "+p.getName()+" has entered portal #"+id);
        if(id == MotoHub.getInstance().tjPortalID) {
            Bukkit.broadcastMessage("This is the TeamJug portal.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitMonitor(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.YELLOW+event.getPlayer().getDisplayName()+" has left the hub");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the hub");
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        MotoHub.getInstance().getMidi().playMidiForPlayer("rigby", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player && e.getCause() != EntityDamageEvent.DamageCause.VOID) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerOpenInventory(InventoryOpenEvent e) {
        if(e.getInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        //No weather on ANY world
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(MotoHub.getInstance().getWorldSpawn());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        Location loc = event.getTo();
        if (event.isCancelled()) return;

        if (loc == null || loc.getWorld() == null) return;
        if (loc.equals(loc.getWorld().getSpawnLocation())) {
            event.setTo(MotoHub.getInstance().getWorldSpawn());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(MotoHub.getInstance().getWorldSpawn());
    }
}
