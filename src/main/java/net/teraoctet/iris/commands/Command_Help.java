package net.teraoctet.iris.commands;

import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command_Help implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        
        Player player = (Player)sender;
        
        if(commandLabel.equalsIgnoreCase("iris"))
        {
            if(args.length == 0 || args[0].equalsIgnoreCase("1"))
            {
                player.sendMessage(formatMsg.format("<yellow>-------- IRIS -----------------"));
                player.sendMessage(formatMsg.format("<yellow>Index (1/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<gray>-<green>/pos <gray>Affiche votre position"));
                player.sendMessage(formatMsg.format("<gray>-<green>/day <gray>Programme le jour dans le monde en cours"));
                player.sendMessage(formatMsg.format("<gray>-<green>/night <gray>Programme la nuit dans le monde en cours"));
                player.sendMessage(formatMsg.format("<gray>-<green>/sun <gray>Programme le soleil sur le monde en cours"));
                player.sendMessage(formatMsg.format("<gray>-<green>/warp [nomDeWarp] <gray>Point de téleportation"));
                player.sendMessage(formatMsg.format("<gray>-<green>/spawn <gray>Téléporte au spawn du monde actuel"));
                player.sendMessage(formatMsg.format("<gray>-<green>/spawn [worldName] <gray>Téléporte au spawn du monde demandé"));
                player.sendMessage(formatMsg.format("<gray>-<green>/troc [achat|vente] [PRIX] <gray>Declare un troc de commerce"));
                return true;
            }
            if(args[0].equalsIgnoreCase("2"))
            {
                player.sendMessage(formatMsg.format("<yellow>Index (2/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<gray>-<green>/bank <gray>Pour consulter son solde d'emeraude sur son compte"));
                player.sendMessage(formatMsg.format("<gray>-<green>/bank depot [nb_emeraudes] <gray>Dépot d'emeraude sur son compte"));
                player.sendMessage(formatMsg.format("<gray>-<green>/bank retrait [nb_emeraudes] <gray>Retrait d'emeraude du compte"));
                player.sendMessage(formatMsg.format("<gray>-<green>/infobook [BookName] <gray>Drop le book demandé"));
                player.sendMessage(formatMsg.format("<gray>-<green>/infobook list <gray>Liste les books disponible"));
                player.sendMessage(formatMsg.format("<gray>-<green>/sethome <gray>Définit le home par default"));
                player.sendMessage(formatMsg.format("<gray>-<green>/home <gray>Téléporte au home par default"));
                player.sendMessage(formatMsg.format("<gray>-<green>/home home [world] <gray>Téléporte au home par default du monde"));
                player.sendMessage(formatMsg.format("<gray>-<green>/lobby <gray>Téléporte au spawn du lobby"));
                return true;
            }
            if(args[0].equalsIgnoreCase("3"))
            {
                player.sendMessage(formatMsg.format("<yellow>Index (3/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<gray>-<green>/lo [pass] <gray>Bloque ton compte"));
                player.sendMessage(formatMsg.format("<gray>-<green>/delock [pass] <gray>Débloque ton compte"));
                player.sendMessage(formatMsg.format("<gray>-<green>/register [pass] [pass]<gray>Enregistrement sur le serveur"));
                player.sendMessage(formatMsg.format("<gray>-<green>/parcelle <gray>Liste les commandes Parcelle"));
                player.sendMessage(formatMsg.format("<gray>-<green>/horde <gray>Liste les commandes Horde"));
                player.sendMessage(formatMsg.format("<gray>-<green>/tpa [joueur] <gray>Demande autorisation de se TP sur 'Joueur'"));
                player.sendMessage(formatMsg.format("<gray>-<green>/tphere [joueur] <gray>Demande autorisation de TP 'Joueur' sur toi"));
                player.sendMessage(formatMsg.format("<gray>-<green>/tp accept <gray>Accept la demande de TP"));
                player.sendMessage(formatMsg.format("<gray>-<green>/tp deny <gray>Refuse la demande de TP"));
                return true;
            }
            if(args[0].equalsIgnoreCase("4"))
            {
                player.sendMessage(formatMsg.format("<yellow>Index (4/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/playeffect <gray>Met un effet fun sur le joueur"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/back <gray>Téléporte aux coordonnées précédente"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/sethome [homename] <gray>définit les homes supplémentaire"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/home [homename] <gray>Téléporte sur les homes supplémentaire"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/home [homename] [world] <gray>Téléporte au home supplémentaire du monde"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/head [nom_du_mhf|NomJoueur] <gray>Donne un MHF ou une tête de joueur"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/print [ligne 0|1|2|3] [texte] : <yellow>modifie le texte d'un panneau"));
                player.sendMessage(formatMsg.format("<gold>VIP<gray>-<green>/fly <gray>Active ou désactive la commande vol"));
                player.sendMessage(formatMsg.format("<gray>-<green>"));
                return true;
            }
            if(args[0].equalsIgnoreCase("5"))
            {
                player.sendMessage(formatMsg.format("<yellow>Index (5/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/tp [player] <gray>Téléporte sur 'player'"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/tp [player] [otherplayer] <gray>Téléporte 'player sur 'otherplayer'"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/tp [x y z] <gray>Téléporte aux coordonnées X, Y, Z"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/ci [otherplayer] [Materiel] <gray>Supprime un item de l'inventaire"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/invsee [player] <gray>Ouvre l'inventaire d'un joueur"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/kill [player] <gray>Tue un joueur"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/setspawn <gray>Enregistre le spawn"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/setwarp [nomDeWarp] <gray>enregistre un point de téleportation"));
                player.sendMessage(formatMsg.format("<gray>-<green>"));
                return true;
            }
            if(args[0].equalsIgnoreCase("6"))
            {
                player.sendMessage(formatMsg.format("<yellow>Index (6/6) - tape /iris [index]"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/trocpublic [achat|vente] [PRIX] <gray>Declare un troc de commerce public"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/playerinfo [player] <gray>renvoie des infos sur le joueur"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/grave clear <gray>Efface tous les inventaires des tombes"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/grave info <gray>Renvoie le nombre d'inventaire actif"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/spawner set [CreatureSpawner] <gray>Modifie le type d'un Mobspawner"));
                player.sendMessage(formatMsg.format("<red>ADMIN<gray>-<green>/spawner [CreatureSpawner] <gray>Donne un Mobspawner"));
                player.sendMessage(formatMsg.format("<gray>-<green>"));
                player.sendMessage(formatMsg.format("<gray>-<green>"));
                player.sendMessage(formatMsg.format("<gray>-<green>"));
                return true;
            }
        }
           
 /*
  
  reload:
    description: reload le serveur
    usage: /<command>
  give:
    description: donne un item
    usage: /<command>
 
  passchange:
    description: modifie ton mot de passe
  test:
    description: test
  scoreboard:
    description: test
  am:
    description: gestion AutoMessage
    usage: |
           /<command> add [NomDuGroupeMessage] [N°deLigne] [Message] - ajoute une ligne de message
           /<command> addgroupe [NomDuGroupeMessage] [permission] [actif] - ajoute un groupe/entête de message
           /<command> update [NomDuGroupeMessage] [N°deLigne] [Message] - met à ajour une ligne de message
           /<command> del [NomDuGroupeMessage] - supprime la totalité du groupe de message
           /<command> list - liste les groupes de messages
           /<command> list [NomDuGroupeMessage] - liste le contenu du groupe de message
           /<command> off [NomDuGroupeMessage] - désactive le groupe de message
           /<command> on [NomDuGroupeMessage] - active le groupe de message
           /<command> run - démarre MessageAuto
           /<command> stop - stop MessageAuto
  portal:
    description: administration des portails
    aliases: [p]
    usage: |
           /<command> add [portalName] [book] [message] - ajoute un portail avec book et message
           /<command> add [portalName] [message] - ajoute un portail avec message
           /<command> add [portalName] - ajoute un portail
           /<command> loc [portalName] - met à jour les coordonnées du portail
           /<command> book [portalName] [book] - livre donner au franchissement du portail
           /<command> message [portalName] [message] - message affiché au franchissement du portail
           /<command> to [portalName] - enregistre la destination du portail

  border:
    description: delimite les bordures des map a ne pas franchir
    aliases: [wb]
    usage: |
           /<command> - Liste des commandes
           /<command> <set>
           /<command> set <radiusX> [radiusZ] - definit la bordure dans un rayon dont vous etes le centre.
           /<command> [world] set <radiusX> [radiusZ] <x> <z> - definit la bordure dans un rayon dont vous etes le centre.
           /<command> [world] radius <radiusX> [radiusZ] - change le rayon de la bordure
           /<command> [world] clear - Supprime la bordure de ce monde
           /<command> clear all - Supprime toutes les bordures
           /<command> list - Liste de la totalité des bordures
           /<command> shape <elliptic|rectangular> - definit la forme par defaut.
           /<command> shape <round|square> - same as above, backwards compatible.
           /<command> getmsg - envoie le message par defaut.
           /<command> setmsg <text> - definit le message par defaut.
           /<command> knockback <distance> - distance de renvoie lors du franchissement
           /<command> whoosh <on/off> - on-off des effets de franchissement
           /<command> portal <on/off> - on-off renvoie au coordonnees opposees
           /<command> delay <amount> - timer :delais de controle.
           /<command> wshape [world] <elliptic|rectangular|default> - definit la forme par defaut.
           /<command> wshape [world] <round|square|default> - definit la forme par defaut.
           /<command> wrap [world] <on/off> - can make border crossings wrap around.
           /<command> [world] fill [freq] [pad] [force] - generate world to border.
           /<command> [world] trim [freq] [pad] - trim world outside of border.
           /<command> bypass [player] [on/off] - let player go beyond border.
           /<command> bypasslist - list players with border bypass enabled.
           /<command> remount <amount> - delay before remounting after knockback.
           /<command> fillautosave <seconds> - world save interval for Fill process.
  
    usage:  /tpdeny*/
        
        return false;
    }
}
