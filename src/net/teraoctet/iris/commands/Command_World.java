package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.world.MultiWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command_World implements CommandExecutor
{  
    private final Iris plugin;

    public Command_World(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(commandLabel.equalsIgnoreCase("world"))
        {
            if (args.length == 0)
            {
                sender.sendMessage(formatMsg.format("<yellow>-------- AIDE World -----------------"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world create <yellow>[NomDuMonde] [Type] <gray>Création d'un nouveau monde"));
                sender.sendMessage(formatMsg.format("<gray>-<green>Types: flat, largebiomes, amplified, normal, nether, end, nostructures, [SEED]"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world import <yellow>[NomDuMonde] <gray>Import d'un monde existant"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world remove <yellow>[NomDuMonde] <gray>Retire un monde"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world list <gray>Liste les mondes activés"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world reset <gray>Recharge les mondes actifs"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world tp <yellow>[NomDuMonde] <gray>Téléporte sur le monde"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world flag <gray>Liste les flag"));
                sender.sendMessage(formatMsg.format("<gray>-<green>/world flag <yellow>[NomDuMonde] [flag] [allow|deny] <gray>Enregistre un flag"));
                return true;
            }
            if(args[0].equalsIgnoreCase("create"))
            {
                MultiWorld.create(sender, args);
            }
            if(args[0].equalsIgnoreCase("import"))
            {
                MultiWorld.importw(sender, args);
            }
            if(args[0].equalsIgnoreCase("remove"))
            {
                MultiWorld.remove(sender, args);
            }
            if(args[0].equalsIgnoreCase("list"))
            {
                MultiWorld.list(sender, args);
            }
            if(args[0].equalsIgnoreCase("reset"))
            {
                MultiWorld.reset(sender, args);
            }
            if(args[0].equalsIgnoreCase("tp"))
            {
                MultiWorld.tp(sender, args);
            }
            if(args[0].equalsIgnoreCase("flag"))
            {
                MultiWorld.flag(sender, args);
            }
        }
        return true;
    }
}
