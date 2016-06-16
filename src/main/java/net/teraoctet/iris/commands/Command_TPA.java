package net.teraoctet.iris.commands;

import java.util.ArrayList;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_TPA
implements CommandExecutor
{
    private static final ArrayList<Tpa> Atpa = new ArrayList<>();
    private static final ConfigFile conf = new ConfigFile();
    Iris plugin;
    
    public Command_TPA(Iris plugin)
    {
        this.plugin = plugin;
    }
  
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        final Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("tpa") && player.hasPermission("iris.tpa"))
        {
            try
            {
                Tpa tpa = new Tpa(player,player.getServer().getPlayer(args[0]),"tpa");
                Atpa.add(tpa);
                final int index = Atpa.indexOf(tpa);
                
                Player toplayer = Bukkit.getPlayer(args[0]);
                if (!toplayer.isOnline())
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Ce joueur n'existe pas ou n'est pas en ligne !"));
                    return true;
                }
                if (!player.getLocation().getWorld().getName().equals(toplayer.getLocation().getWorld().getName()))
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Le TP entre 2 mondes est interdit !"));
                    return true;
                }
            
                if (args.length == 0)
                {
                    player.sendMessage(Iris.formatMsg.format("<yellow>Usage : <green>/tpa <green>[NomDuJoueur]"));
                    return true;
                }
                else if (args.length == 1)
                {
                    Player toPlayer = player.getServer().getPlayer(args[0]);
                    player.sendMessage(Iris.formatMsg.format("<yellow>Veuillez patienter, demande envoy<e_ai>e ..."));
                    toPlayer.sendMessage(Iris.formatMsg.format("<yellow>Acceptez vous que <aqua><player> se t<e_ai>l<e_ai>porte sur vous ?",player));
                    toPlayer.sendMessage(Iris.formatMsg.format("<yellow>Vous avez 30s pour accepter cette demande en tapant : <green>/tpaccept <yellow>refuse : /tpdeny"));
                                        
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Atpa.remove(index);
                                player.sendMessage(Iris.formatMsg.format("<aqua>Temps d'attente dépassé<e_ai> ..."));
                            }
                            catch(Exception ex)
                            {
                                
                            }
                        }
                    }, 600);                   
                }
                return true;
            }
            catch (IllegalArgumentException ex)
            {
                player.sendMessage(Iris.formatMsg.format("<green>Ce joueur est hors ligne !"));
                return true;
            }
        }
        else if (cmd.getName().equalsIgnoreCase("tpaccept") && player.hasPermission("iris.tpaccept"))
        {
            Player toPlayer = null;
            Player fromPlayer = null;
            int i = 0;
            int index = 0;
            
            for(Tpa tpa:Atpa)
            {
                if(tpa.ToPlayer() == player && "tpa".equals(tpa.Type()))
                {
                    fromPlayer = tpa.FromPlayer();
                    toPlayer = player;
                    index = i;
                }
                else if(tpa.FromPlayer() == player && "tphere".equals(tpa.Type()))
                {
                    fromPlayer = player;
                    toPlayer = tpa.ToPlayer();
                    index = i;
                }
                
                i++;
            }
            
            if (fromPlayer == null && toPlayer == null) 
            {
                player.sendMessage(Iris.formatMsg.format("<yellow>Aucune demande en cours !"));
                return true;
            }
            
            Location lastLocation = fromPlayer.getLocation();
            conf.setDoubleYAML("userdata",fromPlayer.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
            conf.setDoubleYAML("userdata",fromPlayer.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
            conf.setDoubleYAML("userdata",fromPlayer.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
            conf.setStringYAML("userdata",fromPlayer.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());
            fromPlayer.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdTP")));
            fromPlayer.teleport(toPlayer);
            
            Atpa.remove(index);
            return true;
            
        }
        
        else if (cmd.getName().equalsIgnoreCase("tpdeny"))
        {
            Player toPlayer = null;
            Player fromPlayer = null;
            String type = "";
            int i = 0;
            int index = 0;
            
            for(Tpa tpa:Atpa)
            {
                if(tpa.ToPlayer() == player && "tpa".equals(tpa.Type()))
                {
                    fromPlayer = tpa.FromPlayer();
                    toPlayer = player;
                    type = tpa.Type();
                    index = i;
                }
                else if(tpa.FromPlayer() == player && "tphere".equals(tpa.Type()))
                {
                    fromPlayer = player;
                    toPlayer = tpa.ToPlayer();
                    type = tpa.Type();
                    index = i;
                }
                
                i++;
            }
            
            if (fromPlayer == null && toPlayer == null) 
            {
                player.sendMessage(Iris.formatMsg.format("<yellow>Aucune demande en cours !"));
                return true;
            }
            
            if ("tpa".equals(type))
            {
                fromPlayer.sendMessage(Iris.formatMsg.format("<yellow><player> a refus<e_ai> votre demande !",toPlayer));          
            }
            else
            {
                toPlayer.sendMessage(Iris.formatMsg.format("<yellow><player> a refus<e_ai> votre demande !",fromPlayer));
            }
            Atpa.remove(index);
            return true;
            
        }
        
        if (cmd.getName().equalsIgnoreCase("tphere") && player.hasPermission("iris.tphere"))
        {
            Tpa tpa = new Tpa(player.getServer().getPlayer(args[0]),player,"tphere");
            Atpa.add(tpa);
            final int index = Atpa.indexOf(tpa);
            
            try
            {
                if (!player.getLocation().getWorld().getName().equals(Bukkit.getPlayer(args[0].toString()).getLocation().getWorld().getName()))
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Le TP entre 2 mondes est interdit !"));
                    return true;
                }
            
                if (args.length == 0)
                {
                    player.sendMessage(Iris.formatMsg.format("<yellow>Usage : <green>/tphere <green>[NomDuJoueur]"));
                    return true;
                }
                else if (args.length == 1)
                {
                    Player toPlayer = player.getServer().getPlayer(args[0]);
                    player.sendMessage(Iris.formatMsg.format("<yellow>Veuillez patienter, demande envoy<e_ai>e ..."));
                    toPlayer.sendMessage(Iris.formatMsg.format("<yellow>Acceptez vous que <aqua><player> vous t<e_ai>l<e_ai>porte sur lui ?",player));
                    toPlayer.sendMessage(Iris.formatMsg.format("<yellow>Vous avez 30s pour accepter cette demande en tapant : <green>/tpaccept <yellow>refuse : /tpdeny"));
                                        
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Atpa.remove(index);
                                player.sendMessage(Iris.formatMsg.format("<aqua>Temps d'attente dépassé<e_ai> ..."));
                            }
                            catch(Exception ex){}
                        }
                    }, 600);                   
                }
                return true;
            }
            catch (IllegalArgumentException ex)
            {
                player.sendMessage(Iris.formatMsg.format("<green>Ce joueur est hors ligne !"));
            }
        }
        return false;
    }
}
    
class Tpa
{
    private final Player fromPlayer;
    private final Player toPlayer;
    private final String Type;
    
    public Tpa(Player fromPlayer, Player toPlayer, String Type)
    {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.Type = Type;
    }
    
    public Player FromPlayer()
    {
        return fromPlayer;
    }
    
    public Player ToPlayer()
    {
        return toPlayer;
    }
    
    public String Type()
    {
        return Type;
    }
}
    

