package net.teraoctet.iris.commands;

import java.util.ArrayList;
import java.util.Set;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Command_Spawner   
implements CommandExecutor
{
    Iris plugin;
    private static final ConfigFile conf = new ConfigFile();

    public Command_Spawner(Iris plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if ((sender instanceof Player))
        {
            Player player = (Player)sender;
            if (!player.hasPermission("iris.spawner"))
            {
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
                return true;
            }
            if ((args.length == 2) && (args[0].equalsIgnoreCase("set")))
            {
                try
                {
                    EntityType type = EntityType.valueOf(args[1].toUpperCase());
                
                    Block b = player.getTargetBlock((Set<Material>) null, 10);
                    if (b == null)
                    {
                        player.sendMessage(Iris.formatMsg.format("<aqua>Vous devez mettre le pointeur sur un block !"));
                        return true;
                    }
                    if (!b.getType().equals(Material.MOB_SPAWNER))
                    {
                        ItemStack spawner = new ItemStack(Material.MOB_SPAWNER);
                        player.getInventory().addItem(new ItemStack[] { spawner });
                        player.sendMessage(Iris.formatMsg.format("<aqua>Ce bloc n'est pas un Mobspawner ! un a <e_ai>t<e_ai> ajout<e_ai> a votre inventaire!"));
                        return true;
                    }
                    CreatureSpawner s = (CreatureSpawner)b.getState();
                    s.setSpawnedType(type);
                    player.sendMessage(Iris.formatMsg.format("<aqua>Ce Mobspawner est maintenant pour : " + type.toString().toLowerCase() + "!"));
                }
                catch (Exception e)
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Erreur de type : <yellow>BLAZE,SKELETON,CAVE_SPIDER,CHICKEN,COW,ENDERMAN,GHAST,GIANT,PIG,SHEEP,VILLAGER,SLIME,....."));
                    return true;
                }
            }
            else if((args.length == 1) && (!args[0].equalsIgnoreCase("set")))
            {
                ItemStack spawner = new ItemStack(Material.MOB_SPAWNER);
                ItemMeta meta = spawner.getItemMeta();
                ArrayList<String> lore = new ArrayList();
                String type = args[0];
                lore.add(0, type);
                meta.setLore(lore);
                spawner.setItemMeta(meta);
                player.getInventory().addItem(new ItemStack[] { spawner });
                player.updateInventory();
                player.sendMessage(Iris.formatMsg.format("<aqua>Mobspawner ajout<e_ai> a votre inventaire!"));
                return true;
            }
        }
        return false;
    }
}