package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Command_PlayEffect 
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if ((commandLabel.equalsIgnoreCase("playeffect")) && (player.hasPermission("iris.playeffect")|| player.isOp())) 
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250, 2));
            player.playSound(player.getLocation(),Sound.ENTITY_WITHER_SPAWN,250,4);
            player.playSound(player.getLocation(),Sound.AMBIENT_CAVE,250,4);
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdPlayEffect"),player));
        }
        return true;
    }
}
