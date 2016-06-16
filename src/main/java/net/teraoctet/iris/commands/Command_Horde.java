package net.teraoctet.iris.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.horde.HChunk;
import net.teraoctet.iris.horde.Horde;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcast;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

public class Command_Horde implements CommandExecutor
{
    private final Iris plugin;
    private Player p;
    private Horde h;
    private final ArrayList<Invit>Invits = new ArrayList();
    public HashMap<String, Long> cooldowns = new HashMap<>();
    
    public Command_Horde(Iris instance) 
    {
        plugin = instance;
        p = null;
        h = null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        Player player = (Player)sender;
        //if(!player.hasPermission("iris.horde")) return true;
        if(commandLabel.equalsIgnoreCase("horde") || commandLabel.equalsIgnoreCase("h"))
        {
            if((args.length == 0 || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("4")) && player.hasPermission("iris.horde"))
            {
                if (args.length == 0 || args[0].equalsIgnoreCase("1"))
                {
                    player.sendMessage(formatMsg.format("<yellow>-------- AIDE HORDE - /horde ou h/ -----------------"));
                    player.sendMessage(formatMsg.format("<yellow>Index (1/4) - tape /h [index]"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde create <yellow>[NomDeHorde]<gray> ne pas mettre d'espace dans le nom"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde setqg <gray> pour creer son QG"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/qg <gray> pour se TP à son QG"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/claim <gray>Revendique un territoire"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde quit <gray>pour quitter sa horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde info <gray>Stats de sa horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde list <gray>Liste des joueurs de sa horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde list [horde] <gray>Liste des joueurs de la horde"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("2"))
                {
                    player.sendMessage(formatMsg.format("<yellow>Index (2/4) - tape /h [index]"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde invite [joueur] <gray>Invite un joueur à rejoindre sa horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde accept <gray>Accepte une invitation à rejoindre une horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde rancon <gray>Paye une rançon pour se libérer et être TP à son QG"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde setgrade [joueur] [1|2|3|4]  <gray>Monte en grade un joueur"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde tag <gray>Masque son nom à face aux ennemis"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde wanted <gray>Donne les coordonées de l'ennemi qui à volé la flamme"));  
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde enemy [horde] <gray>Entre en guerre avec cette horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde ally [horde] <gray>Envoie ou accepte une demande d'alliance"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde neutral [horde] <gray>Envoie ou accepte une demande de neutralité"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("3"))
                {
                    player.sendMessage(formatMsg.format("<yellow>Index (3/4) - tape /h [index]"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde tracker <gray>Indique l'existance d'un QG ennemi dans les 100 blocks"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde show <gray>Listes de toutes les hordes"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde msg <gray>Affiche le message actuel"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde msg [Le message ...] <gray>Enregistre un message sur le territoire"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde msg NULL <gray>Supprime le message enregistré"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde player [joueur] <gray>Affiche les stats du joueur"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/bank vireh [somme]<gray>Effectue un virement de ton compte sur ta horde"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde verse [somme] [joueur]<gray>Effectue un virement sur un joueur"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/y <gray>Tchat avec sa horde uniquement"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("4"))
                {
                    player.sendMessage(formatMsg.format("<yellow>Index (4/4) - tape /h [index]"));    
                    player.sendMessage(formatMsg.format("<gray>-<green>/yh [horde]<gray>Tchat avec la horde indiquée uniquement"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde kill <gray>pour te suicider"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde recrute [on|off] <gray>Ouvrir/fermer le recrutment auto"));
                    player.sendMessage(formatMsg.format("<gray>-<green>/horde join [NomDeLaHorde] <gray>Pour rejoindre une horde en recrutment ON"));
                    player.sendMessage(formatMsg.format("<gray>+ d'info: <aqua>http://teraoctet.net/mcraft/articles/articles.php?id=3&cat=0"));
                    return true;
                }
                
            }  
            if(args[0].equalsIgnoreCase("create") && player.hasPermission("iris.horde.create"))
            {
                if(args.length == 2 || (args.length == 3 && args[2].equalsIgnoreCase("confirm")))
                {
                    if(hordeManager.HasHorde(player) && args.length == 2)
                    {
                        player.sendMessage(Iris.formatMsg.format("<gray>Vous êtes déjà dans une horde ! taper <yellow>/horde create <green>[NomDeHorde] <yellow>confirm "));
                        return true;
                    }
                    if(args.length == 3)
                    {
                        String name = hordeManager.getHordeName(player);
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).removePermission("iris.horde.message." + name, player.getLocation().getWorld().getName());
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix("<gray>Vagabon :", player.getLocation().getWorld().getName());
                        Horde horde = hordeManager.getHorde(player);
                        Chunk chunk = horde.getSpawnHorde().getChunk();
                        horde.getSpawnHorde().getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
                    }
                    String sql = "REPLACE INTO irishorde (hordeName, world, X1, Y1, Z1, kills, dead)" + 
                                "VALUES ('" + args[1] + "','" + player.getWorld().getName() + "'," + player.getLocation().getX() + "," + player.getLocation().getY() + "," 
                                + player.getLocation().getZ() + ", 0, 0);" ;
                    ConMySQL.executeInsert(sql);

                    String sqlMember = "REPLACE INTO irishordemember (playerUUID, playerName, hordeName, typeMember)" + 
                                "VALUES ('" + player.getUniqueId().toString()+ "','" + player.getName() + "','" + args[1] + "', 1); ";
                    ConMySQL.executeInsert(sqlMember);
                    
                    String group = "iris.horde.message." + args[1];
                    ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addPermission(group,player.getLocation().getWorld().getName());
                    ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix(args[1] + ": ", player.getLocation().getWorld().getName());
                    
                    conf.setIntYAML("horde.yml", "Horde." + args[1] + ".Force", 10 );
                    conf.setIntYAML("horde.yml", "Horde." + args[1] + ".Bank", 10 );
                    conf.setStringYAML("horde.yml", "Horde." + args[1] + ".Autel","NULL");

                    player.sendMessage(Iris.formatMsg.format("<gras><green>Horde : <yellow>Vous venez de creer la horde <green>'" + args[1] + "'"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>Enregistrez maintenant votre QG a un emplacement de votre choix"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>En tapant <green>/horde setqg <yellow>le QG sera enregistr<e_ai> au centre du chunk de votre position"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>Le QG est une zone imprenable de 16x16 blocs"));
                    Iris.hordeManager.loadHordes();   
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde create <yellow>[NomDeHorde]<gray> ne pas mettre d'espace dans le nom"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("setqg") && (player.hasPermission("iris.horde.create") || player.isOp()))
            {
                if(!hordeManager.HasHorde(player))
                {
                    player.sendMessage(Iris.formatMsg.format("<gras><gray>Horde : <yellow>Vous devez creer une horde avant de taper cette commande"));
                    return true;
                }
                if(chunkManager.haveQG(player))
                {
                    player.sendMessage(Iris.formatMsg.format("<gras><gray>Horde : <yellow>Vous n'avez le droit qu'à 1 QG"));
                    return true;
                }
                if(chunkManager.isChunkEnnemi(player.getLocation().getChunk(),hordeManager.getHorde(player)))
                {
                    player.sendMessage(Iris.formatMsg.format("<gras><gray>Horde : <yellow>Revendication impossible ici !"));
                    return true;
                }
                if(args.length == 1)
                {
                    String hordeName = hordeManager.getHordeName(player);                    
                    try 
                    {
                        chunkManager.setQG(player);
                    } 
                    catch (SQLException ex) 
                    {
                        Logger.getLogger(Command_Horde.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    player.sendMessage(Iris.formatMsg.format("<gold>Horde : <gray>QG enregistr<e_ai> pour <gras><gold>" + hordeName));
                    player.sendMessage(Iris.formatMsg.format("<gray>Tu as besoin de frères d'armes pour défendre ton territoire !"));
                    player.sendMessage(Iris.formatMsg.format("<gray>invite les en tapant <gold>/horde recrute [joueur]"));
                    hordeManager.reload();
                    chunkManager.LoadChunk();
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde qg <yellow>[NomDeHorde]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("join") && (player.hasPermission("iris.horde") || player.isOp()))
            {
                if(args.length == 2)
                {
                    Horde hordeR = hordeManager.getHorde(args[1]);
                    if(conf.getIntYAML("horde.yml", "Horde." + hordeR.getHordeName() + ".Recrut", 1) == 1)
                    {
                        if(hordeManager.HasHorde(player) == true)
                        {
                            player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Tu fait déjà partie d'une horde, tu dois la quitter avant de postuler pour une autre"));
                            return true;
                        }                                             

                        String group = "iris.horde.message." + hordeR.getHordeName();
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addPermission(group,player.getLocation().getWorld().getName());
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix(hordeR.getHordeName() + ": ", player.getLocation().getWorld().getName());

                        int force2 = conf.getIntYAML("horde.yml", "Horde." + hordeR.getHordeName() + ".Force", 0 );
                        force2 = force2 + 10;
                        conf.setIntYAML("horde.yml", "Horde." + hordeR.getHordeName() + ".Force", force2 );

                        String perm = "iris.horde.message." + hordeR.getHordeName();
                        broadcast(formatMsg.format("<yellow>" + player.getDisplayName() + "<gray> a rejoint notre horde"),perm);
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<gray>Désolé cette horde est OFF en recrutement"));
                    }
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde join <yellow>[NomDeHorde]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("recrute") && (player.hasPermission("iris.horde") || player.isOp()))
            {
                if(args.length == 2)
                {
                    if(hordeManager.getGrade(player) != 1) 
                    {
                        player.sendMessage(Iris.formatMsg.format("<gray>Vous devez être chef de horde pour taper cette commande"));
                        return true;
                    }
                    
                    Horde hordeR = hordeManager.getHorde(player);
                    
                    if (args[1].equalsIgnoreCase("on"))
                    {
                        conf.setIntYAML("horde.yml", "Horde." + hordeR.getHordeName() + ".Recrut", 1);
                        player.sendMessage(formatMsg.format("<gray>horde recrutement <yellow>ON"));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("off"))
                    {
                        conf.setIntYAML("horde.yml", "Horde." + hordeR.getHordeName() + ".Recrut", 0);
                        player.sendMessage(formatMsg.format("<gray>horde recrutement <yellow>OFF"));
                        return true;
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde recrute <yellow>[on|off]"));
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde recrute <yellow>[on|off]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("setgrade"))
            {
                if(hordeManager.getGrade(player) != 1)
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Vous devez être chef de horde pour taper cette commande"));
                    return true;
                }
                if(args.length == 3)
                {
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p == null)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                    }
                    Horde horde = hordeManager.getHorde(p);
                    Horde hordeOf = hordeManager.getHorde(player);
                    
                    if(horde.getHordeName().equalsIgnoreCase(hordeOf.getHordeName()))
                    {
                        hordeManager.setGrade(p, hordeOf.getHordeName(), Integer.valueOf(args[2]));
                        player.sendMessage(formatMsg.format("<green>" + args[1] + "<gray> a été mis au grade " + args[2]));
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<green>" + args[1] + "<gray> ne fait pas partie de votre Horde"));
                    }
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>principe: -<green>/horde setgrade <yellow>[Joueur] [1|2|3]"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("info") && (player.hasPermission("iris.horde") || player.isOp()))
            {
                if(hordeManager.HasHorde(player))
                {
                    hordeManager.printInfoPlayer(player, player.getDisplayName());
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Tu es Vagabond"));
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("kill") && (player.hasPermission("iris.horde") || player.isOp()))
            {
                player.setHealth(0);
                return true;
            }
            if(args[0].equalsIgnoreCase("quit") && (player.hasPermission("iris.horde.quit") || player.isOp()))
            {
                Horde horde = hordeManager.getHorde(player);
                Chunk chunk = horde.getSpawnHorde().getChunk();
                int grade = 0;
                grade = hordeManager.getGrade(player);
                
                String sql = "DELETE FROM irishordemember WHERE playerUUID = '" + player.getUniqueId().toString() + "'; ";
                ConMySQL.executeUpdate(sql);
                
                String name = hordeManager.getHordeName(player);
                ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).removePermission("iris.horde.message." + name, player.getLocation().getWorld().getName());
                ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix("<gray>Vagabon :", player.getLocation().getWorld().getName());
                
                int force = conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", 0) - 15;
                conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", force );
                                
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<light_purple>" + player.getDisplayName() + " vient de quitter notre horde"),"iris.horde.message." + horde.getHordeName());
                player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Vous êtes maintenant redevenu un vagabon"));
                
                if(hordeManager.getCountMember(horde)-1 == 0)
                {
                    String sqlH = "DELETE FROM irishorde WHERE hordeName = '" + horde.getHordeName() + "'; ";
                    ConMySQL.executeUpdate(sqlH);
                    
                    String sqlC = "DELETE FROM irischunk WHERE hordeName = '" + horde.getHordeName() + "'; ";
                    ConMySQL.executeUpdate(sqlC);
                    
                    horde.getSpawnHorde().getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>La horde " + horde.getHordeName() + " n'a plus de combatant et a donc été dissoute"));
                }
                else
                {
                    if(grade == 1)
                    {
                        if(hordeManager.getCountGrade(horde.getHordeName(),1) == 1)
                        {
                            if(hordeManager.getCountGrade(horde.getHordeName(),2) != 0)
                            {
                                String[] members = hordeManager.listeHorde(horde.getHordeName(),2).split(";");
                                hordeManager.setGrade(members[1],1);
                            }
                            else
                            {
                                if(hordeManager.getCountGrade(horde.getHordeName(),3) != 0)
                                {
                                    String[] members = hordeManager.listeHorde(horde.getHordeName(),3).split(";");
                                    hordeManager.setGrade(members[1],1);
                                }
                                else
                                {
                                    String[] members = hordeManager.listeHorde(horde.getHordeName(),4).split(";");
                                    player.sendMessage("*" + members[1]);
                                    player.sendMessage(members);
                                }
                            }
                        }
                    }
                }
                Iris.hordeManager.reload();
                Iris.chunkManager.reload();
                return true;
            }
            if(args[0].equalsIgnoreCase("damage") && (player.hasPermission("iris.horde.create") || player.isOp()))
            {
                player.sendMessage(Iris.formatMsg.format("<gray>Damage : " + hordeManager.getDamage(hordeManager.getHordeName(player))));
                return true;
            }
            if(args[0].equalsIgnoreCase("list") && (player.hasPermission("iris.horde") || player.isOp()))
            {
                if(args.length == 2)
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Membre de la HORDE " + args[1] + " : " + hordeManager.listeHorde(args[1])));
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Membre de ta HORDE : " + hordeManager.listeHorde(hordeManager.getHordeName(player))));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("invite") && player.hasPermission("iris.horde"))
            {
                if(hordeManager.getGrade(player) != 1) 
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Vous devez être chef de horde pour taper cette commande"));
                }
                Player vagabond = player.getServer().getPlayer(args[1]);
                if (vagabond.isOnline() ==  false)
                {
                    player.sendMessage(Iris.formatMsg.format("<yellow>Ce joueur n'est pas connecté"));
                    return true;
                }
                String hordeName = hordeManager.getHordeName(player);
                Invit invit = new Invit(player,vagabond);
                Invits.add(invit);
                                
                player.sendMessage(Iris.formatMsg.format("<yellow>Invitation envoyée ..."));
                vagabond.sendMessage(Iris.formatMsg.format("<yellow>Vous avez reçu une invitation à rejoindre la horde <green>" + hordeName));
                vagabond.sendMessage(Iris.formatMsg.format("<yellow>Pour accepter l'invtation, tape <green>/horde accept"));  
                return true;
            }
            if (args[0].equalsIgnoreCase("accept") && player.hasPermission("iris.horde"))
            {                
                int i = 0;
                int index = 0;
                
                for(Invit v : Invits)
                {
                    if( v.getVagabond() == player)
                    {
                        String hordeName = hordeManager.getHordeName(v.getHordePlayer());
                        player.sendMessage(Iris.formatMsg.format("<yellow>Vous êtes maintenant membre de la horde " + hordeName));
                        if(hordeManager.HasHorde(player))
                        {
                            Horde horde = hordeManager.getHorde(player);
                            Chunk chunk = horde.getSpawnHorde().getChunk();
                            horde.getSpawnHorde().getWorld().regenerateChunk(chunk.getX(), chunk.getZ());

                            String sql = "DELETE FROM irishordemember WHERE playerUUID = '" + player.getUniqueId().toString() + "'; ";
                            ConMySQL.executeUpdate(sql);

                            String name = hordeManager.getHordeName(player);
                            ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).removePermission("iris.horde.message." + name, player.getLocation().getWorld().getName());
                            ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix("<gray>Vagabon :", player.getLocation().getWorld().getName());

                            int force = conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", 0) - 15;
                            conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", force );

                            org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<light_purple>" + player.getDisplayName() + " vient de quitter notre horde"),"iris.horde.message." + horde.getHordeName());
                            
                            if(hordeManager.getCountMember(horde)-1 == 0)
                            {
                                String sqlH = "DELETE FROM irishorde WHERE hordeName = '" + horde.getHordeName() + "'; ";
                                ConMySQL.executeUpdate(sqlH);

                                String sqlC = "DELETE FROM irischunk WHERE hordeName = '" + horde.getHordeName() + "'; ";
                                ConMySQL.executeUpdate(sqlC);

                                player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>La horde " + horde.getHordeName() + " n'a plus de combatant et a donc été dissoute"));
                            }
                            Iris.hordeManager.reload();
                            Iris.chunkManager.reload();
                        }
                        
                        String sqlMember = "REPLACE INTO irishordemember (playerUUID, playerName, hordeName, typeMember)" + 
                                "VALUES ('" + player.getUniqueId().toString()+ "','" + player.getName() + "','" + hordeName + "', 4); ";
                        ConMySQL.executeInsert(sqlMember);
                        Iris.hordeManager.reload();
                        Iris.chunkManager.reload();
                            
                        String group = "iris.horde.message." + hordeName;
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addPermission(group,player.getLocation().getWorld().getName());
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).setSuffix(hordeName + ": ", player.getLocation().getWorld().getName());
                        
                        int force2 = conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Force", 0 );
                        force2 = force2 + 10;
                        conf.setIntYAML("horde.yml", "Horde." + hordeName + ".Force", force2 );
                                                                    
                        String perm = "iris.horde.message." + hordeName;
                        broadcast(formatMsg.format("<yellow>" + player.getDisplayName() + "<gray> a rejoint notre horde"),perm);
                        index = i;
                    }
                    i++;
                }
                Invits.remove(index);
                return true;
            }
            if (args[0].equalsIgnoreCase("tag") && player.hasPermission("iris.horde.tag"))
            {   
                player.setPassenger(player.getWorld().spawn(player.getLocation(), Snowball.class));
                return true;
            }
            if (args[0].equalsIgnoreCase("wanted") && player.hasPermission("iris.horde"))
            {                
                String hordeName = hordeManager.getHordeName(player);
                if (!"NULL".equals(conf.getStringYAML("horde.yml", "Horde." + hordeName + ".Autel", "NULL")))
                {
                    Player wanted = Bukkit.getPlayer(conf.getStringYAML("horde.yml", "Horde." + hordeName + ".Autel", "NULL"));
                    if(wanted == null)
                    {
                        Horde horde = hordeManager.getHorde(player);
                        conf.setStringYAML("horde.yml", "Horde." + hordeName + ".Autel", "NULL");
                        Location beacon = horde.getSpawnHorde();
                        beacon.getBlock().setType(Material.BEACON);
                        return true;
                    }
                    player.sendMessage(Iris.formatMsg.format("<yellow>WANTED : " + wanted.getDisplayName()));
                    player.sendMessage(Iris.formatMsg.format("<gray>position actuelle : X = " + wanted.getLocation().getBlockX() + 
                        " Y = " + wanted.getLocation().getBlockY() +
                        " Z= " + wanted.getLocation().getBlockZ() + 
                        " Monde = " + wanted.getLocation().getWorld().getName()));
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>WANTED : Tout va bien, notre autel est en place !"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("rancon") && player.hasPermission("iris.horde"))
            {                
                String hordeName = hordeManager.getHordeName(player);
                Horde horde = chunkManager.getHorde(player.getLocation().getChunk());
                if (conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Bank", 0) > 39)
                {
                    if(!hordeManager.HasHorde(player))
                    {
                        player.sendMessage(formatMsg.format("<gray>Vous n'êtes pas dans une horde"));
                        return true;
                    }
                    this.h = hordeManager.getHorde(player);
                    this.p = player;

                    if(this.h == null) return true;

                    player.sendMessage(formatMsg.format("<gray>Ta horde a payer une rançon, tu sera TP dans 20s environ"));
                    int bank = conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Bank", 0);
                    bank = bank - 40;
                    conf.setIntYAML("horde.yml", "Horde." + hordeName + ".Bank", bank);
                    
                    bank = conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Bank", 0);
                    bank = bank + 40;
                    conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Bank", bank);
                    
                    this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            p.teleport(h.getSpawnHorde(2));
                        }
                    }, 200L);
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>Ta horde n'a pas les moyens de payer la rançon pour te libérer"));
                    player.sendMessage(formatMsg.format("<gray>Demande à tes ennemis de t'achever ou bien suicide toi en tapant /h kill"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("tracker") && player.hasPermission("iris.horde") && player.hasPermission("iris.tracker"))
            {                
                
                Horde horde = hordeManager.getHEnnemi(player);
                if (horde != null)
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Vous êtes à moins de 100 blocs de la horde " + horde.getHordeName()));
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Aucun territoire ennemi détecté "));
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("ally") && player.hasPermission("iris.horde") && hordeManager.getGrade(player) < 3) 
            {
                String hordeplayer = hordeManager.getHordeName(player);
                String hordeAlly = hordeManager.getHordeNameToName(args[1]);
                
                String permp = "iris.horde.message." + hordeplayer;
                String perma = "iris.horde.message." + hordeAlly;
                
                if(conf.getIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".ally", 0) == 0)
                {
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".ally", 1 );
                    
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>La horde <aqua>" + hordeplayer + " <gray>à réglé la diplomatie sur Allié <white>pour confirmer taper : /h ally " + hordeplayer),perma);
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Une demande d'alliance à été envoyé à la horde <aqua>" + hordeAlly),permp);
                }
                else
                {
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".ally", 1 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".enemy", 0 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".neutral", 0 );
                    
                    conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".enemy", 0 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".neutral", 0 );
                    
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Nous sommes maintenant allié avec la horde <aqua>" + hordeplayer),perma);
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Nous sommes maintenant allié avec la horde <aqua>" + hordeAlly),permp);
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("neutral") && player.hasPermission("iris.horde") && hordeManager.getGrade(player) < 3) 
            {
                String hordeplayer = hordeManager.getHordeName(player);
                String hordeAlly = hordeManager.getHordeNameToName(args[1]);
                
                String permp = "iris.horde.message." + hordeplayer;
                String perma = "iris.horde.message." + hordeAlly;
                
                if(conf.getIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".neutral", 1) == 0)
                {
                    if(conf.getIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".enemy", 0) == 1)
                    {
                        conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".neutral", 1 );
                        
                        org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>La horde <aqua>" + hordeplayer + " <gray>à réglé la diplomatie sur Neutre <white>pour confirmer taper : /h neutral " + hordeplayer),perma);
                        org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Une demande de diplomatie neutre à été envoyé à la horde <aqua>" + hordeAlly),permp);
                        return true;
                    }
                    if(conf.getIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".ally", 0) == 1)
                    {
                        conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".neutral", 1 );
                        conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".ally", 0 );
                        
                        conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".ally", 0 );
                        conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".neutral", 1 );
                        
                        org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>La horde <aqua>" + hordeplayer + " <gray>à changé la diplomatie sur Neutre"),perma);
                        org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Notre diplomatie à changé sur neutre envers la horde <aqua>" + hordeAlly),permp);
                        return true;
                    }
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>Notre diplomatie est déjà sur Neutre envers cette horde"));
                    return true;
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("enemy") && player.hasPermission("iris.horde") && hordeManager.getGrade(player) < 2) 
            {
                String hordeplayer = hordeManager.getHordeName(player);
                String hordeAlly = hordeManager.getHordeNameToName(args[1]);
                
                String permp = "iris.horde.message." + hordeplayer;
                String perma = "iris.horde.message." + hordeAlly;
                
                if(conf.getIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".enemy", 0) == 0)
                {
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".enemy", 1 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".neutral", 0 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeplayer + "." + hordeAlly + ".ally", 0 );
                    
                    conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".enemy", 1 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".ally", 0 );
                    conf.setIntYAML("horde.yml", "Horde." + hordeAlly + "." + hordeplayer + ".neutral", 0 );
                    
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>La horde <aqua>" + hordeplayer + " <gray>nous a déclaré la guerre"),perma);
                    org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Nous avons déclaré la guerre à la horde <aqua>" + hordeAlly),permp);
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>Nous sommes déjà en guerre avec cette horde"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("player") && player.hasPermission("iris.horde"))
            {
                String hordeName = hordeManager.getHordeName(args[1]);
                player.sendMessage(formatMsg.format("<gray>" + args[1] + " est membre de la horde " + hordeName));
                hordeManager.printInfoPlayer(player, args[1]);
                return true;

            }
            if(args[0].equalsIgnoreCase("msg") && player.hasPermission("iris.horde"))
            {
                String message = "";
                for (int i = 1; i < args.length; i++)
                {
                    message = message + ' ';
                    message = message + args[i];
                }                 
                try 
                {
                    if(chunkManager.setMessage(player, message) == true)player.sendMessage(formatMsg.format("<gray>Message enregistré"));
                    else 
                    {
                        HChunk chunk = chunkManager.getChunk(player.getLocation());
                        player.sendMessage(formatMsg.format("<gray>Message actuel : <yellow>" + chunk.getMessage() + " <green- /horde msg [Message à afficher ...]"));
                        player.sendMessage(formatMsg.format("<gray>Pour désactiver le message : <green>/horde msg NULL"));
                    }
                } catch (SQLException ex) 
                {
                    Logger.getLogger(Command_Horde.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("show") && player.hasPermission("iris.horde"))
            {
                player.sendMessage(formatMsg.format(hordeManager.getAllHordes()));
                return true;
            }
            if(args[0].equalsIgnoreCase("kick") && player.hasPermission("iris.horde"))
            {
                Horde horde = hordeManager.getHorde(player);
                
                int grade = 0;
                grade = hordeManager.getGrade(player);
                if (grade != 1)
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Vous n'avez pas le grade suffisant pour cette commande"));
                    return true;
                }
                String hordeNamePlayer = horde.getHordeName();
                String hordeNamePlayerKick = hordeManager.getHordeName(args[1]);
                
                if (hordeNamePlayerKick == null)
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Ce joueur n'existe pas ou n'a pas de horde"));
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>tape: /horde list pour voir les joueurs de ta horde"));
                    return true;
                }
                
                if (hordeNamePlayer.equalsIgnoreCase(hordeNamePlayerKick))
                {
                    String sql = "DELETE FROM irishordemember WHERE playerName = '" + args[1] + "'; ";
                    ConMySQL.executeUpdate(sql);
                
                    String name = hordeManager.getHordeName(player);
                    ru.tehkode.permissions.bukkit.PermissionsEx.getUser(args[1]).removePermission("iris.horde.message." + hordeNamePlayerKick, player.getLocation().getWorld().getName());
                    ru.tehkode.permissions.bukkit.PermissionsEx.getUser(args[1]).setSuffix("<gray>Vagabon :", player.getLocation().getWorld().getName());

                    int force = conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", 0) - 15;
                    conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", force );

                    org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<light_purple>" + args[1] + " a été exclu de notre horde"),"iris.horde.message." + horde.getHordeName());
                    
                    Iris.hordeManager.reload();
                    Iris.chunkManager.reload();
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Ce joueur n'est pas dans ta horde"));
                    player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>tape: /horde list pour voir les joueurs de ta horde"));
                    return true;
                }
                return true;
            }
        }
        if(commandLabel.equalsIgnoreCase("claim") && player.hasPermission("iris.horde.create"))
        {
            if(!hordeManager.HasHorde(player))
            {
                player.sendMessage(Iris.formatMsg.format("<gray>Vous êtes un Vagabond !"));
                return true;
            }
            if(chunkManager.isChunkEnnemi(player.getLocation().getChunk(),hordeManager.getHorde(player)))
            {
                player.sendMessage(Iris.formatMsg.format("<gras><gray>Horde : <yellow>Revendication impossible ici !"));
                return true;
            }
            
            if(chunkManager.droitChunk(player) >  0.0)
            {
                if(chunkManager.ChunkExist(player.getLocation()))
                {
                    if(!chunkManager.getChunk(player.getLocation()).getHordeName().equals(hordeManager.getHordeName(player)) && chunkManager.getChunk(player.getLocation()).getTypeMember() != 1)
                    {
                        String perm = "iris.horde.message." + chunkManager.getChunk(player.getLocation()).getHordeName();
                        org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<red>ALERTE : <gray>Des ennemis revendiquent un de nos territoire!"),perm);
                        player.sendMessage(Iris.formatMsg.format("<gras><green>Horde : <yellow>** Territoire ennemi revendiqué **"));
                    }
                }
                
                int grade = 4;
                if (args.length == 1)
                {
                    grade = Integer.valueOf(args[0]);
                }
                try 
                {
                    chunkManager.claim(player,grade);
                    player.sendMessage(Iris.formatMsg.format("<gras><green>Horde : <yellow>Revendication enregistr<e_ai> !"));

                } catch (SQLException ex) {
                    Logger.getLogger(Command_Horde.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                player.sendMessage(Iris.formatMsg.format("<gray>Horde : <yellow>Force insufisante, revendication impossible !"));
            }
            return true;
        }
        if(commandLabel.equalsIgnoreCase("qg"))
        {
            if (player.hasPermission("iris.horde.qg"))
            {
                int cooldownTime = conf.getIntYAML("config.yml","CoolDownTime",120);
                if(cooldowns.containsKey(player.getDisplayName())) 
                {
                    long secondsLeft = ((cooldowns.get(player.getDisplayName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
                    if(secondsLeft>0) {
                        player.sendMessage(formatMsg.format("<gray>Vous ne pouvez pas utiliser cette commande avant " + secondsLeft + " secondes!"));
                        return true;
                    }
                }
                cooldowns.put(player.getDisplayName(), System.currentTimeMillis());
            }
            if(!"NULL".equals(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL")))
            {
                player.sendMessage(formatMsg.format("<gray>Tu ne peut pas utiliser cette commande quand tu possèdes la flamme d'un ennemi"));
                return true;
            }
            if(!hordeManager.HasHorde(player))
            {
                player.sendMessage(formatMsg.format("<gray>Vous n'êtes pas dans une horde"));
                return true;
            }
            this.h = hordeManager.getHorde(player);
            this.p = player;
            if(this.h == null) return true;
            
            player.sendMessage(formatMsg.format("<gray>QG : Vous serez TP dans 20s environ"));
            
            plugin.scheduleTP(p, 200L, h.getSpawnHorde(2));
            return true;
        }
        if(commandLabel.equalsIgnoreCase("y") && player.hasPermission("iris.horde"))
        {
            String hordeName = hordeManager.getHordeName(player);
            String perm = "iris.horde.message." + hordeName;
            
            String message = "";
            for (int i = 0; i < args.length; i++)
            {
                message = message + ' ';
                message = message + args[i];
            }                 
            org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Msg Privé horde : <white>" + message),perm);
            return true;
        }
        if(commandLabel.equalsIgnoreCase("yh") && player.hasPermission("iris.horde"))
        {
            String hordeName = hordeManager.getHordeName(player);
            String perm = "iris.horde.message." + hordeName;
            
            Horde horde = hordeManager.getHorde(args[0]);
            if(horde == null)
            {
                player.sendMessage(formatMsg.format("<gray>Cette horde n'existe pas !"));
                return true;
            }
            String perm2 = "iris.horde.message." + horde.getHordeName();
                        
            String message = "";
            for (int i = 1; i < args.length; i++)
            {
                message = message + ' ';
                message = message + args[i];
            }                 
            org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Msg Privé horde <aqua>" + horde.getHordeName() + " <gray>: <white>" + message),perm);
            org.bukkit.Bukkit.broadcast(formatMsg.format("<gray>Msg Privé horde <aqua>" + horde.getHordeName() + " <gray>: <white>" + message),perm2);
            return true;
        }
        //player.sendMessage(formatMsg.format("<gray>Vous n'avez pas le grade suffisant pour utiliser cette commande ou bien cette commande n'existe pas"));
        return true;
    }   
}

class Invit
{
    private final Player hordePlayer;
    private final Player vagabond;
        
    public Invit(Player hordePlayer, Player vagabond)
    {
        this.hordePlayer = hordePlayer;
        this.vagabond = vagabond;
    }
    
    public Player getHordePlayer()
    {
        return hordePlayer;
    }
    
    public Player getVagabond()
    {
        return vagabond;
    }
}
