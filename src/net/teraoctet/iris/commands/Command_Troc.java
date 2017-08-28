package net.teraoctet.iris.commands;

import java.util.ArrayList;
import java.util.List;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Command_Troc 
implements CommandExecutor
{    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("troc") && (sender.isOp() || sender.hasPermission("iris.troc")))
        {
            if(args.length == 2)
            {
                String TypeTroc = "";
                switch (args[0]) 
                {
                    case "achat":
                        TypeTroc = "ACHAT";
                        break;
                    case "vente":
                        TypeTroc = "VENTE";
                        break;
                    default:
                        player.sendMessage(formatMsg.format("<gray>Principe : <green>/troc [vente|achat] [prix] <gray>- Vous devez taper <yellow>vente <gray>ou <yellow>achat"));
                        return true;
                }
                String prix1 = args[1];

                ItemStack is = player.getItemInHand();
                ItemMeta meta = is.getItemMeta();

                meta.setDisplayName(player.getName());

                ArrayList<String> lore = new ArrayList();
                String libelle = formatMsg.format("<green>" + TypeTroc + " [" + is.getType().name() + "] au prix de:<yellow> " + prix1 + " Emeraudes");

                lore.add(0, libelle);
                
                meta.setLore(lore);
                is.setItemMeta(meta);
                player.getInventory().setItemInHand(is); 
                player.updateInventory();

                return true;
            }
            else
            {
                player.sendMessage(formatMsg.format("<gray>Principe : <green>/troc [vente|achat] [prix] <gray>- en emeraude, decimal accept<e_ai>"));
                return true;
            }
        }
        if (commandLabel.equalsIgnoreCase("trocpublic") && (sender.isOp() || sender.hasPermission("iris.troc.public")))
        {
            if(args.length > 1)
            {
                String TypeTroc = "";
                switch (args[0]) 
                {
                    case "achat":
                        TypeTroc = "ACHAT";
                        break;
                    case "vente":
                        TypeTroc = "VENTE";
                        break;
                    default:
                        player.sendMessage(formatMsg.format("<gray>Principe : <green>/trocpublic [vente|achat] [prix] <gray>- Vous devez taper <yellow>vente <gray>ou <yellow>achat"));
                        return true;
                }
                String prix1 = args[1];

                ItemStack is = player.getItemInHand();
                ItemMeta meta = is.getItemMeta();
                List<String> lore = is.getItemMeta().getLore();
                
                String info = "";
                if(args.length > 2)
                {
                    info = args[2];
                }
                else 
                {
                    info = is.getType().name();
                }
                
                meta.setDisplayName(formatMsg.format("<green>" + TypeTroc + " [" + info + "]:<yellow> " + 
                        prix1 + " Emeraudes"));
                meta.setLore(lore);
                
                is.setItemMeta(meta);
                player.getInventory().setItemInHand(is); 
                player.updateInventory();

                return true;
            }
            else
            {
                player.sendMessage(formatMsg.format("<gray>Principe : <green>/trocpublic [vente|achat] [prix] <gray>- en emeraude, decimal accept<e_ai>"));
                return true;
            }
        }
        return true;
    }
}
