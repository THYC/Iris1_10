package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.parcelle.ParcelleManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_SetJail 
implements CommandExecutor
{
            
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        ParcelleManager parcelleManager = ParcelleManager.getSett(player);
    
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format("<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"));
            return true;
        }
        if (args.length == 1)
        {
            if (commandLabel.equalsIgnoreCase("setjail") && (sender.isOp() || sender.hasPermission("iris.setjail")))
            {
                if (args.length == 0)
                {
                    sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /setjail <jailname>"));
                    return true;
                }
                String name = args[0];
                if (parcelleManager.hasParcelle(name) == false)
                {
                    Location[] c = {parcelleManager.getBorder1(), parcelleManager.getBorder2()};
                    if ((c[0] == null) || (c[1] == null))
                    {
                        player.sendMessage(formatMsg.format("<green>Vous n'avez pas d<e_ai>fini la bordure"));
                        player.sendMessage(formatMsg.format("<green>Vous pouvez définir les angles opposés en utilisant la pelle en bois"));
                        return true;
                    }
                    
                    int y1 = (int)c[0].getY();
                    int y2 = (int)c[1].getY();
                            
                    String sql = "INSERT INTO irisparcelle (parcelleName, parent, world, X1, Y1, Z1, X2, Y2, Z2, jail, noFly, message)" + 
                                " VALUES ('" + name + "','','" + player.getWorld().getName() + "'," +
                                parcelleManager.getBorder1().getX() + "," + c[0].getY() + "," + parcelleManager.getBorder1().getZ() + "," +
                                parcelleManager.getBorder2().getX() + "," + c[1].getY() + "," + parcelleManager.getBorder2().getZ() + 
                                ",1,1,'<red><gras>PRISON'); ";

                    ConMySQL.executeInsert(sql);

                    String uuid = "NULL";
                    String owner = "NULL";

                    String sqlMember = "INSERT INTO irisparcellemember (playerUUID, playerName, parcelleName, typeMember)" + 
                                "VALUES ('" + uuid + "','" + owner + "','" + args[0] + "',1); ";

                    ConMySQL.executeInsert(sqlMember);
                    
                    player.sendMessage(formatMsg.format("<green>JAIL: La prison est maintenant prot<e_ai>g<e_ai> du niveau Y: " + y1 + " à " + y2));
                    player.sendMessage(formatMsg.format("<green>Jail <yellow>" + args[0] + " actif"));
                    Iris.parcelleManager.loadParcelle();
                            
                    conf.setStringYAML("jail.yml",args[0] + ".world",player.getWorld().getName());
                    conf.setDoubleYAML("jail.yml",args[0] + ".X",player.getLocation().getX());
                    conf.setDoubleYAML("jail.yml",args[0] + ".Y",player.getLocation().getY());
                    conf.setDoubleYAML("jail.yml",args[0] + ".Z",player.getLocation().getZ());
                }
                else
                {
                    sender.sendMessage(Iris.formatMsg.format("<dark_aqua>vous êtes sur une zone déjà protégée"));
                }
            }
            else
            {          
                sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /setjail <jailname>"));
            }
        }
        return true;
    }
}

