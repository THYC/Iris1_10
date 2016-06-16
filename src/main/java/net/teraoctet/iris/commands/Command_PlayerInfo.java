package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_PlayerInfo
implements CommandExecutor
{
    private final Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
  
    public Command_PlayerInfo(Iris plugin)
    {
      this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length == 1)
        {
            if (commandLabel.equalsIgnoreCase("playerinfo") && (sender.isOp() || sender.hasPermission("iris.playerinfo")))
            {
                Player targetPlayer = Bukkit.getPlayerExact(args[0]);
                if (!(sender instanceof Player))
                {
                    if (targetPlayer == null)
                    {
                        Iris.log.info(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),targetPlayer));
                    }
                    else
                    {
                        String playerName = targetPlayer.getDisplayName();
                        boolean playerIsFlying = this.plugin.flyMap.containsKey(targetPlayer);
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Player: " + playerName,targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>PlayerUUID: " + targetPlayer.getUniqueId().toString()));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Fly Mode: " + playerIsFlying,targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Sneak Mode: " + targetPlayer.isSneaking(),targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>World : " + targetPlayer.getWorld(),targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Position XYZ: " + targetPlayer.getLocation().getBlockX() + " " + targetPlayer.getLocation().getBlockY() + " " + targetPlayer.getLocation().getBlockZ(),targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Level : " + targetPlayer.getLevel(),targetPlayer));
                        Iris.log.info(Iris.formatMsg.format("<dark_gray>Adresse : " + targetPlayer.getAddress()));
                    }
                }
                else
                {
                    Player player = (Player)sender;
                    if (targetPlayer == null)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                    }
                    else
                    {
                        String playerName = targetPlayer.getDisplayName();
                        boolean playerIsFlying = this.plugin.flyMap.containsKey(targetPlayer);
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>Player: " + playerName,player));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>PlayerUUID: " + targetPlayer.getUniqueId().toString()));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>Fly Mode: " + playerIsFlying,player));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>Sneak Mode: " + targetPlayer.isSneaking(),player));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>World : " + targetPlayer.getWorld(),player));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>Level : " + targetPlayer.getLevel(),player));
                        sender.sendMessage(Iris.formatMsg.format("<dark_gray>Adresse : " + targetPlayer.getAddress()));
                    }
                            
                }
            } 
        }
        else 
        {
            sender.sendMessage("Usage: /playerinfo <player>");
        }
        return true;
    }
}

