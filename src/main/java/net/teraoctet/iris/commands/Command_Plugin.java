package net.teraoctet.iris.commands;

import java.util.ArrayList;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.utils.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Command_Plugin  implements CommandExecutor
{
    private final Iris plugin;
  
    public Command_Plugin(Iris plugin)
    {
        this.plugin = plugin;
        if ((this instanceof Listener)) 
        {
            Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        if (args.length < 2)
        {
            sender.sendMessage(formatMsg.format("<yellow>-------- AIDE Plugin -----------------"));
            sender.sendMessage(formatMsg.format("<gray>-<green>/plugin load <yellow>[Plugin] <gray>Charge le plugin"));
            sender.sendMessage(formatMsg.format("<gray>-<green>/plugin enable <yellow>[Plugin] <gray>Active le plugin"));
            sender.sendMessage(formatMsg.format("<gray>-<green>/plugin disable <yellow>[Plugin] <gray>Desactive le plugin"));
            sender.sendMessage(formatMsg.format("<gray>-<green>/plugin unload <yellow>[Plugin] <gray>Decharge le plugin"));
            sender.sendMessage(formatMsg.format("<gray>-<green>/plugin reload <yellow>[Plugin] <gray>Recharge le plugin"));
            return true;
        }
        Plugin pl = Bukkit.getServer().getPluginManager().getPlugin(args[1]);
        String message = null;
        if (pl == null)
        {
            if (args[0].equalsIgnoreCase("load"))
            {
                if (!sender.hasPermission("iris.plugin")) 
                {
                    return true;
                }
                message = PluginUtil.load(args[1]);
                sender.sendMessage(message + args[1]);
                return true;
            }
            sender.sendMessage(args[1] + " Non trouvé");
            return true;
        }
        ArrayList<String> mesl = null;
        String pn = pl.getName();
          
        if (args[0].equalsIgnoreCase("enable") && (sender.hasPermission("iris.plugin") || sender.isOp()))
        {
            message = PluginUtil.enable(Bukkit.getPluginManager().getPlugin(pn));
        }
        else if (args[0].equalsIgnoreCase("disable") && (sender.hasPermission("iris.plugin") || sender.isOp()))
        {
            message = PluginUtil.disable(Bukkit.getPluginManager().getPlugin(pn));
        }
        else if (args[0].equalsIgnoreCase("unload") && (sender.hasPermission("iris.plugin") || sender.isOp()))
        {
            message = PluginUtil.unload(Bukkit.getPluginManager().getPlugin(args[1]));
        }
        else if (args[0].equalsIgnoreCase("reload")  && (sender.hasPermission("iris.plugin") || sender.isOp()))
        {
            message = PluginUtil.reload(Bukkit.getPluginManager().getPlugin(args[1]));
        }
        
        if (message != null) 
        {
            sender.sendMessage(message + args[1]);
        } 
        return true;
    }
}
