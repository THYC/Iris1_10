package net.teraoctet.iris.commands;

import java.util.Set;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Command_Lock
implements CommandExecutor
{   
    private final Iris plugin;
  
    public Command_Lock(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("lock") && (sender.isOp() || sender.hasPermission("iris.lock")))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("Vous ne pouvez pas utiliser cette commande ici");
                return true;
            }
            
            Block block = player.getTargetBlock((Set<Material>) null, 100);
            if ((block == null) || (block.getState() == null))
            {
                player.sendMessage(formatMsg.format("<yellow>Vous devez regarder conteneur (curseur plac<_ai> sur un coffre,four,etabli....)"));
                return true;
            }
            
            if (args.length == 0)
            {
                player.sendMessage(formatMsg.format("<yellow>/lock <green>[pass] <gray> verrouille le conteneur avec un mot de passe"));
                return true;
            }
                     
            int X = block.getX();
            int Y = block.getY();
            int Z = block.getZ();
 
            String pass = args[0] + "ee";
            player.sendMessage(String.valueOf(block.getMetadata("Lock:")));
            block.setMetadata("Lock:", new FixedMetadataValue(plugin, pass));
            player.sendMessage(String.valueOf(block.getMetadata("Lock:")));
            player.sendMessage(String.valueOf(block.getMetadata("Lock:").isEmpty()));
            //player.performCommand("blockdata " + X + " " + Y + " " + Z +  " {Lock:\"" + args[0] + "\"}");
            //player.sendMessage(String.valueOf(block.getMetadata("Lock:")));
        }
        return true;
    }
}
