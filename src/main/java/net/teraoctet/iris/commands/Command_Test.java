/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.teraoctet.iris.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.teraoctet.iris.HealthBar;
import net.teraoctet.iris.InfoBook;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.parcelleManager;
import net.teraoctet.iris.world.CoordXZ;
import net.teraoctet.iris.world.WorldFileData;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class Command_Test  implements CommandExecutor
{
    Iris plugin;
    InfoBook infoBook = new InfoBook();
    private HealthBar health;
    
    public Command_Test(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("test") && (sender.isOp() || sender.hasPermission("iris.admin")))
        {
            //player.setCustomName("-");
            //player.setDisplayName("-");
            //player.setCustomNameVisible(true);
            //player.setCustomName("-");
            //player.awardAchievement(Achievement.THE_END);
            //player.sendMessage(String.valueOf(player.getStatistic(Statistic.DROP, Material.SKULL_ITEM)));
            if(args[0].equalsIgnoreCase("1"))
            {
                
                player.setResourcePack("http://teraoctet.net/minecraft/Equanimity.zip");
                
            }
            
            if(args[0].equalsIgnoreCase("2"))
            {
                
                player.setResourcePack("http://teraoctet.net/multigame/minecraft/MinecraftGCL.zip");
            }
            
            
            if(args[0].equalsIgnoreCase("3"))
            {
                player.awardAchievement(Achievement.THE_END);
            }
            
            if(args[0].equalsIgnoreCase("4"))
            {
                
                player.openInventory(plugin.inv);
            }
            if(args[0].equalsIgnoreCase("5"))
            {
                //WorldFileData wfd = null;
                //CoordXZ xz = new CoordXZ(0,0);
                //wfd.testImage(xz, null);
                parcelleManager.listParcelle(player, true);
            }
            
            /*ItemMeta Tracker = player.getPlayer().getItemInHand().getItemMeta();
            Iris.log.info(Tracker.getDisplayName());
            if (Tracker.getDisplayName().equalsIgnoreCase("§c§lTRACKER"))
            {
                player.sendMessage(Tracker.getDisplayName());
                
            }*/
            
            
            //player.setPassenger(player.getWorld().spawn(player.getLocation(), Snowball.class));
            //player.sendMessage("ok");
            //player.sendMessage(String.valueOf(player.getStatistic(Statistic.DROP, Material.SKULL_ITEM)));
            /*//if (entity instanceof TNTPrimed) {
            player.getWorld().createExplosion(player.getLocation(), 0);
            //plugin.Stat();
            Chunk ch;
            ch = player.getLocation().getChunk();
            player.sendMessage(String.valueOf(ch.getX()) + " - " + String.valueOf(ch.getZ() + " - " + ch.toString()));
            ch.getWorld().createExplosion(player.getEyeLocation(), 6f);
            //ch.getChunkSnapshot().*/
            
        }
        
        if(commandLabel.equalsIgnoreCase("scoreboard"))
        {
            Scoreboard board = getServer().getScoreboardManager().getNewScoreboard();
            Objective ob = board.registerNewObjective("menu", "dummy");
            ob.setDisplaySlot(DisplaySlot.SIDEBAR);
            ob.setDisplayName("Nom");
            ob.getScore(getServer().getOfflinePlayer(ChatColor.RED+"Le premier")).setScore(1);
            ob.getScore(getServer().getOfflinePlayer(ChatColor.GREEN+"Le deuxième")).setScore(2);
            ob.getScore(getServer().getOfflinePlayer(ChatColor.AQUA+"Le troisième")).setScore(3);
            ob.getScore(getServer().getOfflinePlayer(ChatColor.BOLD+"Le quatrième")).setScore(4);
            ((Player)sender).setScoreboard(board);
        }
        
        return false;
    }
    
    public Map<String, Integer> taskID = new HashMap<>();

    //call this to schedule the task
    public void scheduleRepeatingTask(final Player p, long ticks){
      final int tid = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
        @Override
        public void run(){
          p.performCommand("qg");
        }
      },ticks); //schedule task with the ticks specified in the arguments

      taskID.put(p.getName(), tid); //put the player in a hashmap
    }

                            //@Override
                            
                                //player.teleport(worldInstance.getSpawnLocation());
                           
    //call this to end the task
    public void endTask(Player p)
    {
        if(taskID.containsKey(p.getName()))
        {
            int tid = taskID.get(p.getName()); //get the ID from the hashmap
            plugin.getServer().getScheduler().cancelTask(tid); //cancel the task
            taskID.remove(p.getName()); //remove the player from the hashmap
        }
    }
}
