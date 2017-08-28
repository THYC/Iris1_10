package net.teraoctet.iris.commands;

import java.util.Set;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.parcelle.Parcelle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_MsgFlotant
implements CommandExecutor
{   
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("mf") && (sender.isOp() || sender.hasPermission("iris.mf")))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("Vous ne pouvez pas utiliser cette commande ici");
                return true;
            }
            
            if (args.length == 0)
            {
                player.sendMessage(formatMsg.format("<yellow>/mf <green>[message] <gray> Affiche le tag d'une entite invisible"));
                return true;
            }
                           
            String message = "";
            for (int i = 0; i < args.length; i++)
            {
                if (i != 0) 
                {
                  message = message + ' ';
                }
                message = message + args[i];
            } 
            
            Block block = player.getTargetBlock((Set<Material>) null, 100);
            if ((block == null) || (block.getState() == null))
            {
                player.sendMessage(formatMsg.format("<yellow>Vous devez regarder un block (curseur plac<_ai> sur un block)"));
                return true;
            }
                        
            try
            {
                int X = block.getX();
                int Y = block.getY() + 1;
                int Z = block.getZ();
                player.performCommand("summon ArmorStand " + X + " " + Y + " " + Z + " {NoGravity:true,Invisible:true,CustomNameVisible:true,CustomName:\"" + message + "\",color:yellow}");
            }
            catch(Exception ex)
            {
                player.sendMessage(formatMsg.format("<yellow>/mf <green>[message] <gray> Affiche le tag d'une entite invisible"));
                return true;
            }
        }
        return true;
    }
}
