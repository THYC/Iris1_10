package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Fly
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("fly") && (sender.isOp() || sender.hasPermission("iris.fly")) && (args.length == 0)) 
        {
            if (player.getAllowFlight())
            {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(Iris.formatMsg.format("<dark_gray>Fly désactivé",player));
            }
            else if (!player.getAllowFlight())
            {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(Iris.formatMsg.format("<dark_gray>Fly activé",player));
            }
            else if (args.length == 1)
            {
                Player target = sender.getServer().getPlayer(args[0]);
                if (target == null)
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Player offline",player));
                }
                else
                {
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    if (targetPlayer.getAllowFlight())
                    {
                        targetPlayer.setAllowFlight(false);
                        targetPlayer.setFlying(false);
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","onFlyDesactivate"),player));
                    }
                    else if (!targetPlayer.getAllowFlight())
                    {
                        targetPlayer.setAllowFlight(true);
                        targetPlayer.setFlying(true);
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","onFlyActivate"),player));
                    }
                }
                return true;
            }
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
    }
}

