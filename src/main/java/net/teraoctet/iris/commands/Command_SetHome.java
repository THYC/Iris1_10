package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_SetHome
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        
        Player player = (Player)sender;
        String home = "home";
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        if (args.length == 0)
        {
            home = "home";
        }
        else
        {
            if (sender.isOp() || sender.hasPermission("iris.home.multiple"))
            {
                home = args[0];
            }
            else
            {
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdMultiHome","<aqua> Vous devez <e_cir>tre V.I.P pour avoir plusieurs HOME !"),player)); 
                return true;
            }
        }
        if (commandLabel.equalsIgnoreCase("sethome") && (sender.isOp() || sender.hasPermission("iris.sethome")))
        {  
            Location homeLocation = player.getLocation();
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + homeLocation.getWorld().getName() + "." + home + ".X", homeLocation.getX());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + homeLocation.getWorld().getName() + "." + home + ".Y", homeLocation.getY());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + homeLocation.getWorld().getName() + "." + home + ".Z", homeLocation.getZ()); 
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdSetHome","<aqua>Home définit !"),player));

        }
        else 
        {
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /sethome <Homename>",player));
        }
        return true;
    }
}

