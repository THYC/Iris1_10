package net.teraoctet.iris.commands;

import java.text.DecimalFormat;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Pos 
implements CommandExecutor 
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] split) 
    {
        
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (commandLabel.equalsIgnoreCase("pos") && (sender.isOp() || sender.hasPermission("iris.pos")))
        {
            if (split.length == 0) {
                Location location = player.getLocation();           
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdPos"),player));
                player.sendMessage(Iris.formatMsg.format("<gold>x = <gray>" + formatDouble(location.getX()),player));
                player.sendMessage(Iris.formatMsg.format("<gold>y = <gray>" + formatDouble(location.getY()),player));
                player.sendMessage(Iris.formatMsg.format("<gold>z = <gray>" + formatDouble(location.getZ()),player));
                return true;
            } 
            else 
            {
                return false;
            }
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }  
    }
    
    public String formatDouble(double i)
    {
    	String formatDouble = new DecimalFormat("0").format(i);
    	return formatDouble;
    }
}
