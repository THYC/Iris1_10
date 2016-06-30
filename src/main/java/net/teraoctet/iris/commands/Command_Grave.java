package net.teraoctet.iris.commands;

import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Grave 
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("grave") && (sender.isOp() || sender.hasPermission("iris.grave")))
        {
            if (args.length == 0)
            {
                player.sendMessage(Iris.formatMsg.format("<yellow>/grave clear <gray>- efface les tombes actives"));
                player.sendMessage(Iris.formatMsg.format("<yellow>/grave info <gray>- liste les tombes actives"));
                return true;
            }
            else if(args.length == 1)
            {
                switch (args[0]) 
                {
                    case "clear":
                        //GraveListener.removeInventory();
                        return true;
                    case "info":
                        //player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdGraveInfo","<aqua>Nombre de tombe active : "), player) + String.valueOf(GraveListener.inventorys.size()));
                        return true;
                }
            }
        }
        return true;
    }
}

