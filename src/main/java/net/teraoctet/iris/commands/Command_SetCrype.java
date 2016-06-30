package net.teraoctet.iris.commands;

import java.util.List;
import java.util.Set;
import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.parcelle.ParcelleManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_SetCrype
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
        
    @Override
    @SuppressWarnings("null")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        
        Player player = (Player)sender;
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        if (commandLabel.equalsIgnoreCase("setcrypte") && (sender.isOp() || sender.hasPermission("iris.setcrypte")))
        {  
            Block block = player.getTargetBlock((Set<Material>) null, 100);
            
            if ((block == null) || (block.getState() == null) || (!(block.getState() instanceof Chest)))
            {
                player.sendMessage(formatMsg.format("<yellow>Vous ne regardez pas un coffre"));
                return true;
            }
            Location crypteLocation = block.getLocation();
            int X = crypteLocation.getBlockX();
            int Y = crypteLocation.getBlockY();
            int Z = crypteLocation.getBlockZ();
            
            //Location signLocation = blockSign.getLocation();
            //int X2 = signLocation.getBlockX();
            //int Y2 = signLocation.getBlockY();
            //int Z2 = signLocation.getBlockZ();
            
            String world = crypteLocation.getWorld().getName();
            int pos = 1;
            
            Set<String> crypteList = conf.getKeysYAML("crypte.yml","crypte");
            if(!crypteList.isEmpty())
            {
                int count = crypteList.size();
                pos = count + 1;
            }
            
            conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".X",X);
            conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Y",Y);
            conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Z",Z);
            //conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".X2",X2);
            //conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Y2",Y2);
            //conf.setIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Z2",Z2);
            conf.setStringYAML("crypte.yml","crypte." + String.valueOf(pos) +".world",world);
            
            /*if ((blockSign == null) || (blockSign.getState() == null) || (!(blockSign.getState() instanceof Sign)))
            {
                Sign sign = (Sign)blockSign.getState();
                sign.setLine(0,formatMsg.format("<dark_purple>Ci git :",player));
                sign.update();
            }*/
            
            player.sendMessage(Iris.formatMsg.format("<aqua>Crypte définit !",player));

        }
        else 
        {
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Fait un click gauche avec la pelle en bois sur le coffre puis tape :",player));
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /setcrypte",player));
        }
        return true;
    }
}

