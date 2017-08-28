package net.teraoctet.iris.commands;

import java.util.ArrayList;
import java.util.List;
import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class Command_AS
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
        
    @Override
    @SuppressWarnings("null")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs")));
            return true;
        }
        Player player = (Player)sender;
        
        if (sender.isOp() || sender.hasPermission("iris.admin.as"))
        {  
            if(args.length == 0)
            {
                player.sendMessage(Iris.formatMsg.format("<aqua>usage: /as <name> [boolean small]"));
                return true;
            }
            
            Location loc = player.getEyeLocation();
            
            ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta)is.getItemMeta();
            meta.setOwner(player.getName());
            is.setItemMeta(meta);
            
            String name = args[0];
            boolean small = false;
            
            if(args.length > 1)small = Boolean.valueOf(args[1]);
                                    
            double coef = 57.295779513082323D;
            ArmorStand as = (ArmorStand)loc.add(0, 1, 0).getWorld().spawn(loc.add(0, 1, 0), ArmorStand.class);
            as.setVisible(true);
            as.setCanPickupItems(true);
            as.setRemoveWhenFarAway(false);
            as.setArms(true);
            as.setBasePlate(false);
            as.setCustomName(ChatColor.AQUA + name.replace("_", " "));
            as.setCustomNameVisible(true);
            as.setGravity(true);
            as.setSmall(small);
            as.setInvulnerable(true);
            as.setCanPickupItems(true);
            as.setHelmet(is);
            as.setGlowing(false);
            //as.setItemInHand(new ItemStack(Material.WOOD_AXE, 1, (byte)0));
            //as.setRightArmPose(new EulerAngle(-40.0D / coef, 42.0D / coef, -17.0D / coef));
            //as.setHeadPose(new EulerAngle(-40.0D / coef, 52.0D / coef, -17.0D / coef));
            //as.setLeftLegPose(new EulerAngle(-40.0D / coef, 42.0D / coef, -17.0D / coef));

            //player.sendMessage(Iris.formatMsg.format("<aqua> définit !",player));
            List<String> msg = new ArrayList<String>();
            conf.setListYAML("armorstand.yml",ChatColor.AQUA + name,msg);
        }
        return true;
    }
}

