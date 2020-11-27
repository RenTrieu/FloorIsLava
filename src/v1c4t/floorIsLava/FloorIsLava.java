package v1c4t.floorIsLava;

import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import net.md_5.bungee.api.ChatColor;

public class FloorIsLava extends JavaPlugin 
    implements Listener, CommandExecutor {
    
    // Keeps track of all blocks placed by the player
    // Also includes spawn blocks
    private ArrayList<Block> placedBlockList;
    private BukkitScheduler scheduler;
    
    /*
     * Initializes scheduler and placedBlockList
     * and registers plugin with the server
     */
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,  this);
        
        for (String command : getDescription().getCommands().keySet()) {
            getServer().getPluginCommand(command).setExecutor(this);
        }
        
        this.scheduler = getServer().getScheduler();
        this.placedBlockList = new ArrayList<Block>();
    }
    
    
    /*
     * Handles command inputs to turn on/off the FloorIsLava challenge
     */
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (command.getName().equalsIgnoreCase("floorislava")) {
            if (args.length != 1) {
                sendInvalid(sender);
                return false;
            }
        }
        return false;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

    }
    
    private void sendInvalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid usage. Please use:");
        sender.sendMessage(ChatColor.RED + "/floorislava on");
        sender.sendMessage(ChatColor.RED + "/floorislava off");
    }
    
    /*
     * Runnable that checks to see if players are on a manually placed block
     * or spawn block. If they are not, kills them.
     */
    private class FloorLavaTask implements Runnable {

    }
}
