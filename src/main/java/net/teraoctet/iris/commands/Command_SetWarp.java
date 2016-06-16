package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_SetWarp 
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdWarpNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        if (args.length == 1)
        {
            if (commandLabel.equalsIgnoreCase("setwarp") && (sender.isOp() || sender.hasPermission("iris.setwarp")))
            {
                if(conf.IsConfigYAML("warp.yml", args[0]))
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdWarpExist"),player));
                }
                else
                {
                    conf.setStringYAML("warp.yml",args[0] + ".world",player.getWorld().getName());
                    conf.setDoubleYAML("warp.yml",args[0] + ".X",player.getLocation().getX());
                    conf.setDoubleYAML("warp.yml",args[0] + ".Y",player.getLocation().getY());
                    conf.setDoubleYAML("warp.yml",args[0] + ".Z",player.getLocation().getZ());
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdSetWarp"),player));
                }

            }
            else 
            {
                sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /warp <warpname>",player));
            }
        }
        else
        {          
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /setwarp <warpname>",player));
        }
        return true;
    }
}

