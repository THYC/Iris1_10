package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import static org.bukkit.Bukkit.getOfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_dejail implements CommandExecutor
{    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        //Player player = (Player) sender;
        Player playerJail = Bukkit.getPlayer(args[0]);
        String playerUUID = "";
        
        for(Player playerOnline : Bukkit.getServer().getOnlinePlayers())
        {
            if(playerOnline.getDisplayName() == null ? args[0] == null : playerOnline.getDisplayName().equals(args[0]))
            {
                playerUUID = playerJail.getUniqueId().toString();
            }
            else
            {
                playerUUID = getOfflinePlayer(args[0]).getUniqueId().toString();
            }
        }
                                
        if (args.length > 0)
        {
            if (commandLabel.equalsIgnoreCase("dejail") && (sender.isOp() || sender.hasPermission("iris.dejail")))
            {
                conf.setStringYAML("userdata",playerUUID + ".yml", "jail", "null");   
                broadcastMessage(formatMsg.format("<yellow>" + args[0] + " a ete libéré de prison"));
            }    
            else
            {
                sender.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
                return true;
            }
        }
        else 
        {
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /dejail <player>"));
        }
        return true;
    }
}
