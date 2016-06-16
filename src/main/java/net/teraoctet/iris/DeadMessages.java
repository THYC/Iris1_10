package net.teraoctet.iris;

import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeadMessages
implements Listener
{    
    private static final ConfigFile conf = new ConfigFile();
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {      
        if (event.getEntityType() != EntityType.PLAYER) 
        {
            return;
        }
        Player player = event.getEntity();
        String dMsg = event.getDeathMessage();
        Location location = player.getLocation();
        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", location.getX());
        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", location.getY());
        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", location.getZ());
        conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", location.getWorld().getName());               
        
        if (dMsg == null)
        {
            return;
        }
        
        if (dMsg.contains("died"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "died"),player));
        }
        else if (dMsg.contains("drowned"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "drowned"),player));
        }
        else if (dMsg.contains("hit the ground too hard"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "hit-the-ground-too-hard"),player));
        }
        else if (dMsg.contains("was slain by"))
        {
            String[] s = dMsg.split("\\s+");
            if (player.getServer().getPlayer(s[4]) != null)
            {
                String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-slain-by"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
            else
            {
              String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-slain-by-mob"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
        }
        else if (dMsg.contains("was shot by"))
        {
            String[] s = dMsg.split("\\s+");
            if (player.getServer().getPlayer(s[4]) != null)
            {
                String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-shot-by"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
            else
            {
                String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-shot-by-mob"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
        }
        else if (dMsg.contains("was killed by"))
        {
            String[] s = dMsg.split("\\s+");
            if (player.getServer().getPlayer(s[4]) != null)
            {
                String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-killed-by"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
            else
            {
                String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-killed-by-mob"),player);
                event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
            }
        }
        else if (dMsg.contains("was fireballed by"))
        {
            String[] s = dMsg.split("\\s+");
            String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-fireballed-by-mob"),player);
            event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
        }
        else if (dMsg.contains("fell out of the world"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "fell-out-of-world"),player));
        }
        else if (dMsg.contains("tried to swim in lava"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "tried-to-swin-in-lava"),player));
        }
        else if (dMsg.contains("went up in flames"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "went-up-in-flames"),player));
        }
        else if (dMsg.contains("burned to death"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "burned-to-death"),player));
        }
        else if (dMsg.contains("blew up"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "blew-up"),player));
        }
        else if (dMsg.contains("was blown up by Creeper"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "creeper"),player));
        }
        else if (dMsg.contains("was killed by magic"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-killed-by-magic"),player));
        }
        else if (dMsg.contains("suffocated in a wall"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "suffocated-in-a-wall"),player));
        }
        else if (dMsg.contains("was pricked to death"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-pricked-to-death"),player));
        }
        else if (dMsg.contains("starved to death"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "starved-to-death"),player));
        }
        else if (dMsg.contains("was shot by arrow"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-shot-by-arrow"),player));
        }
        else if (dMsg.contains("withered away"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "withered-away"),player));
        }
        else if (dMsg.contains("was killed while trying to hurt"))
        {
            String[] s = dMsg.split("\\s+");
            String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-killed-while-trying-to-hurt"),player);
            event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
        }
        else if (dMsg.contains("was squashed by a falling anvil"))
        {
            event.setDeathMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-squashed-by-a-falling-anvil"),player));
        }
        else if (dMsg.contains("was pummeled by"))
        {
            String[] s = dMsg.split("\\s+");
            String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "was-pummeled-by"),player);
            event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
        }
        else if (dMsg.contains("fell from a high place"))
        {
            String[] s = dMsg.split("\\s+");
            String msg = Iris.formatMsg.format(conf.getStringYAML("messages.yml", "fell_from_a_hight_place","<dark_red>[<mort>]<dark_gray> <player> s'est pris pour un volatile et s'est rammass<e_ai>"),player);
            event.setDeathMessage(msg.replaceAll("<killer>", s[4]));
        }
        else
        {
            event.setDeathMessage(dMsg);
            Bukkit.getConsoleSender().sendMessage(formatMsg.format("<aqua>" + dMsg));
        }
    }
}

