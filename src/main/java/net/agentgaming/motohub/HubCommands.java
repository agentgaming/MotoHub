package net.agentgaming.motohub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommands implements CommandExecutor {

    @SuppressWarnings("FieldCanBeLocal")
    private MotoHub plugin;

    public HubCommands(MotoHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        if(cmd.getName().equalsIgnoreCase("spawn")) {
            if(args.length > 0) {
                String player = args[0];
                Player p = Bukkit.getPlayer(player);
                if(p == null) {
                    sender.sendMessage(ChatColor.RED + "Could not find '" + player + "'.");
                } else {
                    p.teleport(MotoHub.getInstance().getWorldSpawn());
                }
            } else {
                ((Player) sender).teleport(MotoHub.getInstance().getWorldSpawn());
            }
            return true;
        }

        return false;
    }

}
