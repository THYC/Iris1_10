package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_SetSpawn
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (!(sender instanceof Player))
        {
          sender.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        if (commandLabel.equalsIgnoreCase("setspawn") && (sender.isOp() || sender.hasPermission("iris.setspawn")))
        {
            Location spawn = player.getLocation();
            player.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            player.getWorld().setSpawnFlags(true, true);
            conf.setIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.x", spawn.getBlockX());
            conf.setIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.y", spawn.getBlockY()+1);
            conf.setIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.z", spawn.getBlockZ());
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdSetSpawn","<dark_aqua>Spawn d<e_ai>finit !"),player));
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
    } 
}
