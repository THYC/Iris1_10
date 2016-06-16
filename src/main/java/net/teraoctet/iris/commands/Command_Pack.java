/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author THYC
 */
public class Command_Pack  implements CommandExecutor
{
    private final Iris plugin;
    
    public Command_Pack(Iris instance) 
    {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        Player player = (Player)sender;
        if(args.length == 0)
        {
            player.sendMessage(formatMsg.format("<yellow><gras> ---- Liste des packs textures disponible :"));
            player.sendMessage(formatMsg.format("<gray>/pack 1 <yellow> --> Equanimity"));
            player.sendMessage(formatMsg.format("<gray>/pack 2 <yellow> --> MinecraftGCL"));
            player.sendMessage(formatMsg.format("<gray>/pack 3 <yellow> --> full_of_life"));
            player.sendMessage(formatMsg.format("<gray>/pack 4 <yellow> --> MassiveCraft-Medieval-V7"));
            player.sendMessage(formatMsg.format("<gray>/pack 5 <yellow> --> LIFE-HD-1.7"));
            player.sendMessage(formatMsg.format("<gray>/pack 6 <yellow> --> Infinite RPG_1.7.8_ 1.0.1a"));
            player.sendMessage(formatMsg.format("<gray>/pack 7 <yellow> --> Watch_Dogs_512x512"));
            player.sendMessage(formatMsg.format("<gray>/pack 8 <yellow> --> MauZi-Realistic-1.7"));
            return true;
        }
        
        if(args[0].equalsIgnoreCase("1"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>Equanimity"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/Equanimity.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/Equanimity.zip");
        }

        if(args[0].equalsIgnoreCase("2"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>MinecraftGCL"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/multigame/minecraft/MinecraftGCL.zip");
            player.setResourcePack("http://teraoctet.net/multigame/minecraft/MinecraftGCL.zip");
        }
        
        if(args[0].equalsIgnoreCase("3"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>full_of_life"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/full_of_life.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/full_of_life.zip");
        }
        
        if(args[0].equalsIgnoreCase("4"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>MassiveCraft-Medieval-V7"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/MassiveCraft-Medieval-V7.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/MassiveCraft-Medieval-V7.zip");
        }
        
        if(args[0].equalsIgnoreCase("5"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>LIFE-HD-1.7"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/LIFE-HD-1.7.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/LIFE-HD-1.7.zip");
        }
        
        if(args[0].equalsIgnoreCase("6"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>Infinite_RPG_1.7.8_ 1.0.1a"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/Infinite_RPG_1.7.8_ 1.0.1a.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/Infinite_RPG_1.7.8_ 1.0.1a.zip");
        }
        
        if(args[0].equalsIgnoreCase("7"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>Watch_Dogs_512x512"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/Watch_Dogs_512x512.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/Watch_Dogs_512x512.zip");
        }
        
        if(args[0].equalsIgnoreCase("8"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous allez installer <yellow>MauZi-Realistic-1.7"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","http://teraoctet.net/minecraft/MauZi-Realistic-1.7.zip");
            player.setResourcePack("http://teraoctet.net/minecraft/MauZi-Realistic-1.7.zip");
        }
        
        if(args[0].equalsIgnoreCase("null"))
        {
            player.sendMessage(formatMsg.format("<gray>Vous avez effacé le chargement du pack"));
            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PackTexture","NULL");
        }
        return true;
    }
}
