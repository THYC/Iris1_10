package net.teraoctet.iris.commands;

import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.parcelle.ParcelleManager;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Portal 
implements CommandExecutor
{
    Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
    
    public Command_Portal(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (!(sender instanceof Player))
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","cmdWarpNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        
        if(args.length == 0)
        {
            player.sendMessage(formatMsg.format("<yellow>-------- AIDE Portal -----------------"));
            player.sendMessage(formatMsg.format("<yellow>Index (1/2) - tape /portal [index]"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal create <yellow>[NomDuPortail] [book] <gray>Enregistre un portail avec un livre associ<e_ai>"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal create <yellow>[NomDuPortail] [book] [Message] <gray>Enregistre un portail avec un livre et un message d'accueil associ<e_ai>"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal book [NomDuPortail]<yellow>[book] <gray>Enregistre un livre associ<e_ai> au portail"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal msg <yellow>[NomDuPortail] [message ...] <gray>Message d'accueil"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal gamemode <yellow>[NomDuPortail] [0|1|2] <gray>Enregistre le mode de jeux"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal to <yellow>[NomDuPortail] <gray>d<e_ai>finie le point d'arriv<e_aie du portail"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal loc <yellow>[NomDuPortail] <gray>modifie l'entr<e_ai>e du portail"));
            player.sendMessage(formatMsg.format("<gray>-<green>/portal reload <gray>recharge les tables"));
            return true;
        }  
        
        if (args.length > 0)
        {
            if (commandLabel.equalsIgnoreCase("portal") && (sender.isOp() || sender.hasPermission("iris.portal.admin")))
            {                
                ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
                
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("create")) 
                {
                    String book = null;
                    String message = "";

                    if (args.length == 3)
                    {
                        book = args[2];
                    }
                    if (args.length > 3)
                    {
                        book = args[2];
                        for (int i = 3; i < args.length; i++)
                        {
                            if (i != 3) 
                            {
                              message = message + ' ';
                            }
                            message = message + args[i];
                        }                 
                    }
                    if (args.length == 1)
                    {
                        player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal add [NomDuPortail] <gray>[book] [message] - gris en option", player));
                        return true;
                    }

                    ParcelleManager parcelleManager = ParcelleManager.getSett(player);
                    Location[] locs = null;
                    locs[0] = parcelleManager.getBorder1();
                    locs[1] = parcelleManager.getBorder2();
                    
                    String sql = "INSERT INTO irisportal (portalName, world, X1, Y1, Z1, X2, Y2, Z2, bookName, message, mode)" + 
                            "VALUES ('" + args[1] + "','" + player.getWorld().getName() + "'," +
                            locs[0].getBlockX() + "," + locs[0].getBlockY() + "," + locs[0].getBlockZ() + "," +
                            locs[1].getBlockX() + "," + locs[1].getBlockY() + "," + locs[1].getBlockZ() + ", '" + book + "','" + message.replaceAll("'", "''") + "',0); ";

                    ConMySQL.executeInsert(sql);
                    player.sendMessage(formatMsg.format("<aqua>portail " + args[1] + " cr<e_ai>e", player));
                    portal.loadPortal();
                    return true;
                }

                if (args[0].equalsIgnoreCase("book") && args.length == 3) 
                {
                    if(args.length == 3)
                    {
                        String sql = "UPDATE irisportal SET bookName='" + args[2] 
                                + "' WHERE portalName='" + args[1] + "';";
                        ConMySQL.executeUpdate(sql);
                        player.sendMessage(formatMsg.format("<aqua>Book enregistr<e_ai>", player));
                        portal.loadPortal();
                        return true;
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal book <aqua>[NomDuPortail] [bookname]"));
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("gamemode")) 
                {
                    if(args.length == 3)
                    {
                        String sql = "UPDATE irisportal SET mode = " + args[1] 
                            + " WHERE portalName='" + args[2] + "';";
                        ConMySQL.executeUpdate(sql);
                        player.sendMessage(formatMsg.format("<aqua>GameMode enregistr<e_ai>", player));
                        portal.loadPortal();
                        //mysql.close();
                        return true;
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal gamemode <aqua>[0/1/2] [NomDuPortail]", player));
                        return true;
                    }
                }

                if ((args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("msg")) && args.length > 2) 
                {
                    String message = "";
                    for (int i = 2; i < args.length; i++)
                    {
                        if (i != 2) 
                        {
                          message = message + ' ';
                        }
                        message = message + args[i];
                    }                 
                    String sql = "UPDATE irisportal SET message = '" + message.replaceAll("'", "''") 
                            + "' WHERE portalName='" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);
                    player.sendMessage(formatMsg.format("<aqua>Message d'accueil enregistr<e_ai>", player));
                    portal.loadPortal();
                    //mysql.close();
                    return true;
                }

                if (args[0].equalsIgnoreCase("to") && args.length == 2) 
                {                        
                    String sql = "UPDATE irisportal SET toworld = '" + player.getWorld().getName()+ "' WHERE portalName = '" + args[1] + "';";      
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET toX1 = " + player.getLocation().getBlockX() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET toY1 = " + player.getLocation().getBlockY() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET toZ1 = " + player.getLocation().getBlockZ() + " WHERE portalName = '" + args[1] + "';";     
                    ConMySQL.executeUpdate(sql);

                    player.sendMessage(formatMsg.format("<aqua>point de t<e_ai>l<e_ai>portation du portail enregistr<e_ai>", player));
                    portal.loadPortal();
                    //mysql.close();
                    return true;
                }

                if (args[0].equalsIgnoreCase("loc") && args.length == 2) 
                {                        
                    ParcelleManager parcelleManager = ParcelleManager.getSett(player);
                    Location[] locs = null;
                    locs[0] = parcelleManager.getBorder1();
                    locs[1] = parcelleManager.getBorder2();

                    String sql = "UPDATE irisportal SET world = '" + locs[0].getWorld().getName()+ "' WHERE portalName = '" + args[1] + "';";      
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET X1 = " + locs[0].getBlockX() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET Y1 = " + locs[0].getBlockY() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET Z1 = " + locs[0].getBlockZ() + " WHERE portalName = '" + args[1] + "';";     
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET X2 = " + locs[1].getBlockX() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET Y2 = " + locs[1].getBlockY() + " WHERE portalName = '" + args[1] + "';";
                    ConMySQL.executeUpdate(sql);

                    sql = "UPDATE irisportal SET Z2 = " + locs[1].getBlockZ() + " WHERE portalName = '" + args[1] + "';";     
                    ConMySQL.executeUpdate(sql);

                    player.sendMessage(formatMsg.format("<aqua>point de t<e_ai>l<e_ai>portation du portail enregistr<e_ai>", player));
                    portal.loadPortal();
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload") && args.length == 1) 
                {                        
                    portal.loadPortal();
                }
                else
                {
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal add <aqua>[NomDuPortail] -<green> ajoute un portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal add <aqua>[NomDuPortail] <gray>[book] -<green> ajoute un portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal add <aqua>[NomDuPortail] <gray>[book] [message] -<green> ajoute un portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal gamemode <aqua>[NomDuPortail] [0/1/2] [NomDuPortail] -<green> d<e_ai>finie le GameMode <a_gr> l'arriv<e_ai>e du portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal to <aqua>[NomDuPortail] -<green> d<e_ai>finie le point d'arriv<e_aie du portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal loc <aqua>[NomDuPortail] -<green> modifie l'entr<e_ai>e du portail"));
                    player.sendMessage(formatMsg.format("<aqua>usage : <yellow>/portal reload -<green> recharge les tables"));
                }
            }
            else
            {
                player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
                return true;
            }
        }
        return true;     
    }
}
