package net.teraoctet.iris.commands;

import java.util.Set;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Print implements CommandExecutor
{
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        Player player = (Player)sender;
        if (player.hasPermission("iris.print") || player.isOp())
        {           
            Block block = player.getTargetBlock((Set<Material>) null, 100);
            if ((block == null) || (block.getState() == null) || (!(block.getState() instanceof Sign)))
            {
                player.sendMessage(formatMsg.format("<yellow>Vous ne regardez pas un panneau"));
                return true;
            }
            
            Sign sign = (Sign)block.getState();
            
            try
            {
                int ligne = Integer.valueOf(args[0]);
                String message = "";
                for (int i = 1; i < args.length; i++)
                {
                    if (i != 1) 
                    {
                      message = message + ' ';
                    }
                    message = message + args[i];
                } 

                sign.setLine(ligne,formatMsg.format(message,player));
                sign.update();
            }
            catch(Exception ex)
            {
                player.sendMessage(formatMsg.format("<yellow>/print <green>[N°Ligne] [Message]"));
                return true;
            }
        }
        return true;
    }
    
}
