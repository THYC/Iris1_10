package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.parcelle.Parcelle;
import net.teraoctet.iris.parcelle.ParcelleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command_Parcelle 
implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        Player player = (Player)sender;
        ParcelleManager parcelleManager = ParcelleManager.getSett(player);
        
        if(commandLabel.equalsIgnoreCase("parcelle"))
        {
            if(args.length == 0 || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("3"))
            {
                if (args.length == 0 || args[0].equalsIgnoreCase("1"))
                {
                    player.sendMessage(formatMsg.format("<yellow>-------- AIDE Parcelle - /parcelle ou /parc -----------------"));
                    player.sendMessage(formatMsg.format("<yellow>Index (1/2) - tape /parc [index]"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle add <yellow>[NomDeParcelle] <gray>Enregistre une parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle add <yellow>[NomDeParcelle] TH <gray>Enregistre une parcelle sur toute la hauteur de la map"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle del <yellow>[NomDeParcelle] <gray>Supprime une parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle msg <yellow>[votre message ...] <gray>Enregistre le message d'accueil"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle msg <gray>Affiche le message d'accueil actuel"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle addplayer <yellow>[Joueur] <gray>Ajoute les droits d'un joueur à la parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle delplayer <yellow>[parcelle] [Joueur] <gray>Supprime les droits d'un joueur à la parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <gray>Affiche la valeur des flags pour cette parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>noenter <yellow>[0|1] <gray>1 = Les joueurs 'default' ne peuvent entrer"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("2"))
                {
                    player.sendMessage(formatMsg.format("<yellow>Index (2/2) - tape /parc [index]"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>nobuild <yellow>[0|1] <gray>1 = Les étrangers ne peuvent pas construire"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>nobreak <yellow>[0|1] <gray>1 = Les étranger ne peuvent pas casser"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>notp <yellow>[0|1] <gray>1 = Les étrangers ne peuvent pas se TP"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>nointerac <yellow>[0|1] <gray>1 = pas d'interaction avec les objets"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>nofire <yellow>[0|1] <gray>1 = Le feu ne détruit pas les blocs"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle flag <yellow>[NomParcelle] <green>nomob <yellow>[0|1] <gray>1 = Pas de spawn de monstres"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle info <gray>Info sur la parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle avendre <yellow>[Prix] <gray>Met en vente la parcelle"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/parcelle changeplayer <yellow>[nomparcelle] [Nom_joueur] <gray>Change le propriétaire de la parcelle"));
                    if(player.hasPermission("iris.admin.parcelle.reload")) player.sendMessage(formatMsg.format("<gray>-<green>/parcelle reload <gray>Recharge les donnees parcelles"));
                }
                return true;
            }  
            if(args[0].equalsIgnoreCase("add") && player.hasPermission("iris.parcelle.add"))
            {
                if(args.length > 1)
                {
                    String name = args[1];
                    if (parcelleManager.hasParcelle(name) == false)
                    {
                        Location[] c = {parcelleManager.getBorder1(), parcelleManager.getBorder2()};
                        if ((c[0] == null) || (c[1] == null))
                        {
                            player.sendMessage(formatMsg.format("<green>Vous n'avez pas d<e_ai>fini la bordure"));
                            player.sendMessage(formatMsg.format("<green>Vous pouvez d<e_ai>finir la bordure en utilisant la pelle en bois"));
                            return true;
                        }
                        
                        if(parcelleManager.parcelleActive(player.getWorld().getName(),parcelleManager.getBorder1(), parcelleManager.getBorder2()) /*&& !player.isOp()*/)
                        {
                            player.sendMessage(formatMsg.format("<green>Vous ne pouvez pas cr<e_ai>er cette parcelle, il y a d<e_ai>ja une parcelle prot<e_ai>g<e_ai> sur cette selection, recommencez"));
                            return true;
                        }
                        
                        int X = (int) Math.round(c[0].getX()-c[1].getX());
                        int Z = (int) Math.round(c[0].getZ()-c[1].getZ());
                        if(X < 0)X = -X;
                        if(Z < 0)Z = -Z;
                        int nbBlock =  (X * Z);
                        int cout = 2;
                        
                        if(nbBlock < 51)
                        {
                            cout = 2;
                        }
                        else if(nbBlock < 101)
                        {
                            cout = 3;
                        }
                        else if(nbBlock < 201)
                        {
                            cout = 4;
                        }
                        else
                        {
                            cout = (nbBlock / 100) * 1;
                        }
                                
                        
                        ItemStack a = new ItemStack(Material.EMERALD, cout);
                        if(player.getInventory().contains(Material.EMERALD,cout) || player.isOp())
                        {
                            if(!player.isOp())
                            {
                                player.getInventory().removeItem(a);
                                player.updateInventory();
                            }

                            int y1 = (int)c[0].getY();
                            int y2 = (int)c[1].getY();
                            
                            if (Math.abs(y2 - y1) <= 4 || "TH".equalsIgnoreCase(args[2]))
                            {
                                player.sendMessage(formatMsg.format("<green>Parcelle: Votre parcelle est maintenant prot<e_ai>g<e_ai> de la bedrock jusqu'au ciel"));
                                c[0].setY(0.0D);
                                c[1].setY(c[1].getWorld().getMaxHeight());
                            }
                            else 
                            {
                                player.sendMessage(formatMsg.format("<green>Parcelle: Votre parcelle est maintenant prot<e_ai>g<e_ai> du niveau Y: " + y1 + " à " + y2));
                            }
                            
                            String sql = "INSERT INTO irisparcelle (parcelleName, parent, world, X1, Y1, Z1, X2, Y2, Z2, message)" + 
                                        "VALUES ('" + args[1] + "','','" + player.getWorld().getName() + "'," +
                                        parcelleManager.getBorder1().getX() + "," + c[0].getY() + "," + parcelleManager.getBorder1().getZ() + "," +
                                        parcelleManager.getBorder2().getX() + "," + c[1].getY() + "," + parcelleManager.getBorder2().getZ() + ",'<red><gras>Parcelle prot<e_ai>g<e_ai>'); ";

                            ConMySQL.executeInsert(sql);
                            
                            String uuid = player.getUniqueId().toString();
                            String owner = player.getName();
                            if(player.isOp())
                            {
                                uuid = "NULL";
                                owner = "NULL";
                            }
                            
                            String sqlMember = "INSERT INTO irisparcellemember (playerUUID, playerName, parcelleName, typeMember)" + 
                                        "VALUES ('" + uuid + "','" + owner + "','" + args[1] + "',1); ";

                            ConMySQL.executeInsert(sqlMember);

                            player.sendMessage(Iris.formatMsg.format("<green>Protection : <yellow>" + args[1] + " protection activ<e_ai>e"));
                            Iris.parcelleManager.loadParcelle();
                        }
                        else
                        {
                            player.sendMessage(Iris.formatMsg.format("<yellow>Le cout de la protection est de " + cout + " Emeraudes, que vous devez avoir dans votre inventaire"));
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<red>Ce nom de parcelle existe d<e_ai>ja"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/parcelle add <yellow>[NomDeParcelle]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("delplayer") && player.hasPermission("iris.parcelle.delplayer"))
            {
                if(args.length == 3)
                {
                    String name = args[1];
                    if (parcelleManager.hasParcelle(name) == true)
                    {
                        if(parcelleManager.getParcelleOwner(name).contains(player.getUniqueId().toString()) || player.isOp())
                        {    
                        
                            String sqlMember = "DELETE FROM irisparcellemember WHERE parcelleName = '" + args[1] + "' AND playerName = '" + args[2] + "'";

                            ConMySQL.executeUpdate(sqlMember);

                            player.sendMessage(Iris.formatMsg.format("<green>Habitant : <yellow>" + args[2] + " supprim<e_ai>e"));
                            Iris.parcelleManager.loadParcelle();
                        }
                        else
                        {
                            player.sendMessage(formatMsg.format("<red>Vous n'êtes pas le propriétaire de cette parcelle"));
                            return true;
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<red>Aucune parcelle enregistr<e_ai> sur ce nom"));
                        return true;
                    }
                }
            }
            if(args[0].equalsIgnoreCase("del") && player.hasPermission("iris.parcelle.del"))
            {
                if(args.length == 2)
                {
                    String name = args[1];
                    if (parcelleManager.hasParcelle(name) == true)
                    {
                        if(parcelleManager.getParcelleOwner(name).contains(player.getUniqueId().toString()) || player.isOp())
                        {    
                        
                            String sql = "DELETE FROM irisparcelle WHERE parcelleName = '" + args[1] + "'; ";
                            ConMySQL.executeUpdate(sql);

                            String sqlMember = "DELETE FROM irisparcellemember WHERE parcelleName = '" + args[1] + "'; ";

                            ConMySQL.executeUpdate(sqlMember);

                            player.sendMessage(Iris.formatMsg.format("<green>Protection : <yellow>" + args[1] + " supprim<e_ai>e"));
                            Iris.parcelleManager.loadParcelle();
                        }
                        else
                        {
                            player.sendMessage(formatMsg.format("<red>Vous n'êtes pas le propriétaire de cette parcelle"));
                            return true;
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<red>Aucune parcelle enregistr<e_ai> sur ce nom"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/parcelle del <yellow>[NomDeParcelle]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("avendre") && player.hasPermission("iris.parcelle.avendre"))
            {
                if(args.length == 2)
                {
                    Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                    if (parcelle != null)
                    {
                        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()) || player.isOp())
                        {
                            String cout = args[1];
                            World newBlock = player.getLocation().getWorld();
                            final Block nb = newBlock.getBlockAt(player.getLocation());

                            nb.setType(Material.SIGN_POST);            
                            Sign sign = (Sign)nb.getState();

                            String Line0 = formatMsg.format("<gras>PARCELLE");
                            String Line1 = formatMsg.format("<gras>A VENDRE");
                            String Line2 = formatMsg.format("PRIX: " + cout);
                            String Line3 = formatMsg.format("EMERAUDE");
                            sign.setLine(0, Line0);
                            sign.setLine(1, Line1);
                            sign.setLine(2, Line2);
                            sign.setLine(3, Line3);
                            sign.update();
                        }
                        else
                        {
                            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne vous appartient pas, vous ne pouvez pas la mettre en vente !"));
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Aucune parcelle enregistr<e_ai> ici!"));
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle avendre <yellow>[prix en emeraude]"));
                }
            }
            if(args[0].equalsIgnoreCase("addplayer") && player.hasPermission("iris.parcelle.addplayer"))
            {
                if(args.length == 2)
                {
                    Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                    if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous devez <e_cir>tre habitant de la parcelle pour executer cette commande>"));
                        return true;
                    }
                    if (parcelle == null)
                    {
                        player.sendMessage(formatMsg.format("<yellow>Aucune parcelle enregistré ici"));
                        return true;
                    }       
                    Player allow = Bukkit.getPlayer(args[1]);
                    
                    if (allow == null)
                    {
                        player.sendMessage(formatMsg.format("<yellow>Ce joueur n'est pas en ligne ou n'existe pas"));
                        return true;
                    }
                    
                    String sqlMember = "INSERT INTO irisparcellemember (playerUUID, playerName, parcelleName, typeMember)" + 
                                "VALUES ('" + allow.getUniqueId().toString()+ "','" + allow.getName() + "','" + parcelle.getName() + "',0); ";

                    ConMySQL.executeInsert(sqlMember);

                    player.sendMessage(Iris.formatMsg.format("<green><player> <yellow>fait maintenant partie  des habitants de <green>" + parcelle.getName(),allow));
                    allow.sendMessage(Iris.formatMsg.format("<green>Vous faites maintenant partie  des habitants de <green>" + parcelle.getName()));
                    Iris.parcelleManager.reload();
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle addplayer <yellow>[Nom_joueur]"));
                }
            }
            if(args[0].equalsIgnoreCase("info"))
            {
                Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                if (parcelle != null)
                {
                    player.sendMessage(formatMsg.format("<gray>Parcelle : " + parcelle.getName()));
                    player.sendMessage(formatMsg.format("<gray>Proprietaire : " + Iris.parcelleManager.getParcelleOwnerName(parcelle)));
                    player.sendMessage(formatMsg.format("<gray>Habitants : " + Iris.parcelleManager.getParcelleAllowedName(parcelle)));
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>aucune parcelle enregistrée ici"));
                    return true;
                }
            }
            if((args[0].equalsIgnoreCase("msg") && (player.hasPermission("iris.parcelle.message")) || player.isOp()))
            {
                if(args.length > 1)
                {
                    Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                    if (parcelle != null && !parcelle.getuuidOwner().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous devez <e_cird>tre sur une parcelle et "
                                + "<e_cird>tre propriétaire de la parcelle pour executer cette commande>"));
                        return true;
                    }
                    String message = "";
                    for (int i = 1; i < args.length; i++)
                    {
                        if (i != 3) 
                        {
                          message = message + ' ';
                        }
                        message = message + args[i];
                    } 
                    if (parcelleManager.setMessage(player, parcelle, message)) player.sendMessage(Iris.formatMsg.format("<green>Message enregistré"));
                }
                else
                {
                    Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                    player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle msg <yellow>[votre message ....] <gray>Pour changer le message"));
                    if (parcelle != null) player.sendMessage(formatMsg.format("<gray>Message actuel : " + parcelle.getMessage(),player));
                }
            }
            if(args[0].equalsIgnoreCase("flag") && (player.hasPermission("iris.parcelle.flag") || player.isOp()))
            {
                if(args.length < 2)
                {
                    player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle flag <white>[NomParcelle] [noenter|nobuild|nobreak|notp|nointerac|nofire|nomob|notnt] [0|1]"));
                    return true;
                }
                if(parcelleManager.hasParcelle(args[1]) == false)
                {
                    player.sendMessage(formatMsg.format("<light_purple>" + args[1] + " n'existe pas, respectez les majuscules et minuscules"));
                    return true;
                }
                if(parcelleManager.getParcelleOwner(args[1]).contains(player.getUniqueId().toString()) || player.isOp())
                {  
                    if (args.length == 4 && ("1".equals(args[3]) || "0".equals(args[3])))
                    {
                        String sql = "";
                        switch (args[2])
                        {
                            case "noenter":
                                sql = "UPDATE irisparcelle SET noEnter = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "nobuild":
                                sql = "UPDATE irisparcelle SET noBuild = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "nobreak":
                                sql = "UPDATE irisparcelle SET noBreak = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;    
                            case "notp":
                                sql = "UPDATE irisparcelle SET noTeleport = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "nointerac":
                                sql = "UPDATE irisparcelle SET noInteract = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "nofire":
                                sql = "UPDATE irisparcelle SET noFire = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "nomob":
                                sql = "UPDATE irisparcelle SET noMob = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            case "notnt":
                                sql = "UPDATE irisparcelle SET noTNT = " + args[3] 
                                    + " WHERE parcelleName = '" + args[1] + "';";
                                break;
                            default :
                                player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle flag <white>[NomParcelle] [noenter|nobuild|nobreak|notp|nointerac|nofire|nomob|notnt] [0|1]"));
                                return true;
                        }
                        ConMySQL.executeUpdate(sql);
                        player.sendMessage(formatMsg.format("<gray>flag enregistré"));
                        Iris.parcelleManager.reload();
                    
                    }
                    else
                    {
                        Parcelle parcelle = parcelleManager.getParcelle(args[1]);
                        player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle flag <white>[NomParcelle] [noenter|nobuild|nobreak|notp|nointerac|nofire|nomob|notnt] [0|1]"));
                        if (parcelle != null) player.sendMessage(formatMsg.format("<gray>Flag enregistré : " + parcelleManager.getFlag(parcelle)));
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<red>Vous n'êtes pas le propriétaire de cette parcelle"));
                }
            }
            if(args[0].equalsIgnoreCase("changeplayer") && player.hasPermission("iris.admin.parcelle.changeplayer"))
            {
                if(args.length == 3)
                {
                    Parcelle parcelle = parcelleManager.getParcelle(args[1]);
                    if (!player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous devez <e_cir>tre OP pour executer cette commande>"));
                        return true;
                    }
                    if (parcelle == null)
                    {
                        player.sendMessage(formatMsg.format("<yellow>Aucune parcelle enregistré sous ce nom"));
                        return true;
                    }       
                    Player allow = Bukkit.getPlayer(args[2]);
                    
                    if (allow == null)
                    {
                        player.sendMessage(formatMsg.format("<yellow>Ce joueur n'est pas en ligne ou n'existe pas"));
                        return true;
                    }
                    String sqlMember = "INSERT INTO irisparcellemember (playerUUID, playerName, parcelleName, typeMember)" + 
                                "VALUES ('" + allow.getUniqueId().toString()+ "','" + allow.getName() + "','" + parcelle.getName() + "',1); ";

                    ConMySQL.executeInsert(sqlMember);
                    
                    String sqlMember2 = "DELETE FROM irisparcellemember WHERE parcelleName = '" + args[1] + "';";

                    ConMySQL.executeInsert(sqlMember2);

                    player.sendMessage(Iris.formatMsg.format("<green><player> <yellow>est maintenant propiétaire de <green>" + parcelle.getName(),allow));
                    allow.sendMessage(Iris.formatMsg.format("<green>Vous êtes maintenant propriétaire de <green>" + parcelle.getName()));
                    Iris.parcelleManager.reload();
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: <green>/parcelle changeplayer <yellow>[nomparcelle] [Nom_joueur]"));
                }
            }
            if(args[0].equalsIgnoreCase("reload") && player.hasPermission("iris.admin.parcelle.reload"))
            {
                Iris.parcelleManager.reload();
            }
        }
        return true;
    }
    
}
