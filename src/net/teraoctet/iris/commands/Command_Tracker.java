package net.teraoctet.iris.commands;

import java.util.ArrayList;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Command_Tracker
implements CommandExecutor
{    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("tracker") && sender.isOp())
        {
            ItemStack is = new ItemStack(345);
            ItemMeta meta = is.getItemMeta();

            meta.setDisplayName(formatMsg.format("<red><gras>TRACKER"));

            ArrayList<String> lore = new ArrayList();
            String libelle = formatMsg.format("<white><italique>Outil de précision fabriqué par les villageois ");
            String libelle2 = formatMsg.format("<dark_green>Clic droit <white>pour tenter de repérer un ennemi à moins de 200 blocs de distance ");
            String libelle3 = formatMsg.format("<dark_green>Clic gauche <white>Affiche la distance entre toi et l'ennemi repéré ");
            lore.add(0, libelle);
            lore.add(1, libelle2);
            lore.add(2, libelle3);

            meta.setLore(lore);
            is.setItemMeta(meta);
            player.getInventory().setItemInHand(is); 
            player.updateInventory();
        }
        return true;
    }
}
