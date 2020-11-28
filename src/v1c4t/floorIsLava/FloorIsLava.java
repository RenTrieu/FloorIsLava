package v1c4t.floorIsLava;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;

public class FloorIsLava extends JavaPlugin 
    implements Listener, CommandExecutor {
    
    // Keeps track of all blocks placed by the player
    // Also includes spawn blocks
    private ArrayList<Block> placedBlockList;
    // True if challenge is on, false if challenge is off
    private Boolean pluginState = false;
    
    /*
     * Initializes scheduler and placedBlockList
     * and registers plugin with the server
     */
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,  this);
        
        for (String command : getDescription().getCommands().keySet()) {
            getServer().getPluginCommand(command).setExecutor(this);
        }
        
        this.placedBlockList = new ArrayList<Block>();
        this.pluginState = false;
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
        else {
            return false;
        }
        
        if (args[0].equalsIgnoreCase("on")) {
            sender.sendMessage(ChatColor.GREEN 
                           + "The FloorIsLava challenge is now activated.");
            for (Player player : getServer().getOnlinePlayers()) {
                Location playerStartLoc = player.getLocation().clone();
                playerStartLoc = playerStartLoc.subtract(0.0, 0.1, 0.0);
                this.placedBlockList.add(playerStartLoc.getBlock());
            }
            this.pluginState = true;
            return true;
        }
        else if (args[0].equalsIgnoreCase("off")) {
            sender.sendMessage(ChatColor.GREEN
                           + "The FloorIsLava challenge is now deactivated.");
            this.pluginState = false;
            return true;
        }
        else {
            sendInvalid(sender);
            return false;
        }
    }
    
    /*
     * When a block is placed, add it to the placedBlockList
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.pluginState == true) {
            this.placedBlockList.add(event.getBlockPlaced());
        }
    }
    
    /*
     * When multiple blocks are placed with a place action, add each component
     * to placedBlockList
     */
    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        if (this.pluginState == true) {
            for (BlockState bState : event.getReplacedBlockStates()) {
                if (!(this.placedBlockList.contains(bState.getBlock()))) {
                    this.placedBlockList.add(bState.getBlock());
                }
            }
        }
    }
    
    /*
     * When a player respawns, exempts the block underneath them
     * from killing them
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location spawnBlockLoc = event.getRespawnLocation().clone()
                                      .subtract(0.0, 0.1, 0.0);
        if (!(this.placedBlockList.contains(spawnBlockLoc.getBlock()))
            && (this.pluginState == true)) {
            this.placedBlockList.add(spawnBlockLoc.getBlock());
        }
    }
    
    /*
     * Kills the player if they move onto a block that isn't in
     * placedBlockList
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location destFloorLoc = event.getTo().clone().subtract(0.0, 0.1, 0.0);
        Block destBlock = destFloorLoc.getBlock();
        Material destMat = destBlock.getBlockData().getMaterial();
        if (!(this.placedBlockList.contains(destBlock))
            && (this.pluginState == true)
            && (destMat != Material.AIR)
            && (!destBlock.isLiquid())
            && (!destBlock.isEmpty())
            && (!destBlock.isPassable())) {
            player.setHealth(0.0);
        }
    }
    
    private void sendInvalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid usage. Please use:");
        sender.sendMessage(ChatColor.RED + "/floorislava on");
        sender.sendMessage(ChatColor.RED + "/floorislava off");
    }
}
