package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_CI
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("ci") && (sender.isOp() || sender.hasPermission("iris.ci")))
        {
            if (args.length == 0)
            {
                player.getInventory().clear();
            }
            else if (args.length == 1)
            {
                Player target = sender.getServer().getPlayer(args[0]);
                if (target == null)
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                }
                else
                {
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    targetPlayer.getInventory().clear();
                }
            }
            else if (args.length == 2)
            {
                Player target = sender.getServer().getPlayer(args[0]);
                if (target == null)
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                }
                else
                {
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    Material material = Material.getMaterial(args[1]);
                    try
                    {
                        targetPlayer.getInventory().remove(material);
                    }
                    catch(Exception e)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "errorMaterial"),player));
                    }
                }
            }
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
        
    }
}

