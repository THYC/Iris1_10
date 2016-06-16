package net.teraoctet.iris.commands;

import java.util.HashMap;
import net.teraoctet.iris.Iris;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command_Give implements CommandExecutor
{  
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if ((sender instanceof Player))
        {
            Player player = (Player)sender;
            if (!player.hasPermission("iris.give"))
            {
                player.sendMessage("");
            }
            else
            {
                if (args.length == 0) 
                {
                    player.sendMessage("§cUsage: /give <Player> <ItemID> <Ammount>");
                }
                if (args.length > 0)
                {
                    Integer ID = null;
                    Short data = null;
                    Integer amount = Iris.conf.getIntYAML("config.yml", "default-stack-size",64);
                    
                    String mat = args[1];

                    String cdata = null;
                    Player p_ = Bukkit.getPlayer(args[0]);
                    
                    if (mat.contains(":"))
                    {
                        String[] mats = mat.split(":");
                        mat = mats[0].trim();
                        cdata = mats[1].trim();
                    }
                    try
                    {
                        ID = Integer.parseInt(mat);
                    }
                    catch (NumberFormatException e)
                    {
                        try
                        {
                            ID = Material.getMaterial(mat.trim().replace(" ", "_").toUpperCase()).getId();
                        }
                        catch (Exception e2)
                        {
                            player.sendMessage("Erreur");
                            return true;
                        }
                    }
                    if (ID == 0)
                    {
                        player.sendMessage("Pas de d'ID !?");
                        return true;
                    }
                    if (Material.getMaterial(ID) == null)
                    {
                        player.sendMessage("ID = Null !?");
                        return true;
                    }
                    if (cdata != null)
                    {
                        try
                        {
                            data = Short.parseShort(cdata);
                        }
                        catch (NumberFormatException e)
                        {
                            player.sendMessage("§cThe metadata was invalid!");
                            return true;
                        }
                        if (data < 0)
                        {
                            player.sendMessage("§cThe metadata was invalid!");
                            return true;
                        }
                    }
                    if (args.length == 3)
                    {
                        Integer amt = null;
                        try
                        {
                            amt = Integer.parseInt(args[2]);
                        }
                        catch (NumberFormatException e)
                        {
                            player.sendMessage("§cInvalid amount!");
                            return true;
                        }
                        if (amt < 1) 
                        {
                            amt = 1;
                        }
                        amount = amt;
                    }
                    ItemStack toInv;
                    if (data != null) 
                    {
                        toInv = new ItemStack(Material.getMaterial(ID), amount, data);
                    } 
                    else 
                    {
                        toInv = new ItemStack(Material.getMaterial(ID), amount);
                    }
                    
                    HashMap<Integer, ItemStack> left = p_.getInventory().addItem(new ItemStack[] { toInv });
                    if (!left.isEmpty()) 
                    {
                        for (ItemStack item : left.values()) 
                        {
                            p_.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                    player.sendMessage("§eGiving " + amount + " of " + Material.getMaterial(ID).toString().toLowerCase().replace("_", " ") + " to " + p_.getName() + ".");
                    return true;
                }
            }
        }
        return true;
    }
}

    
