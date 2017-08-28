package net.teraoctet.iris.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.utils.Economy;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
    
    public class TrocListener 
    implements Listener 
    {
        private final HashMap<String, ArrayList<ChestTroc>> ChestTrocs = new HashMap<>();
        private final Economy economy = new Economy();
      
        @EventHandler
        public void onPlayerInfoTroc(PlayerInteractEvent event)
        {
            try
            {
                if(event.getAction() == Action.LEFT_CLICK_BLOCK)
                {
                    Player player = event.getPlayer();

                    Block interactedBlock = event.getClickedBlock();
                    if (interactedBlock == null) 
                    {
                        return;
                    }
                    Material blockMat = interactedBlock.getType();
                    if (blockMat != Material.CHEST)
                    {
                        return;
                    }

                    Chest chest = (Chest)interactedBlock.getState();

                    if (chest.getInventory().getItem(0) == null) 
                    {
                        return;
                    }
                    if (chest.getInventory().getItem(0).hasItemMeta() == false) 
                    {
                        return;
                    }
                    if (chest.getInventory().getItem(0).getItemMeta().hasLore() == false) 
                    {
                        return;
                    }

                    for(int i = 0; i < 9; i++)
                    {  
                        if(chest.getInventory().getItem(i) != null)
                        {
                            if(chest.getInventory().getItem(i).hasItemMeta())
                            {
                                String[] args = chest.getInventory().getItem(i).getItemMeta().getLore().get(0).split(" ");
                                if (args[0].contains("ACHAT"))
                                {
                                    player.sendMessage(formatMsg.format("<gray>Achete [" +  chest.getInventory().getItem(i).getType().name() + "] pour le prix de : <gras><yellow>" + args[5] + " emeraudes"));
                                }
                                if (args[0].contains("VENTE"))
                                {
                                    player.sendMessage(formatMsg.format("<gray>A Vendre [" +  chest.getInventory().getItem(i).getType().name() + "] pour le prix de : <gras><yellow>" + args[5] + " emeraudes"));
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception ex)
            {
                Iris.log.log(Level.WARNING, "Erreur infoTroc: {0}", ex.getMessage());
            }
        }
                
        @EventHandler()
        public final void onOpenInventory(InventoryOpenEvent event)
        {
            
            if(!"TROC".contains(event.getInventory().getName()))
            {
                return;
            }
            Player player = (Player)event.getPlayer();
            if (event.getInventory().getItem(0) == null) 
            {
                return;
            }
            if (event.getInventory().getItem(0).hasItemMeta() == false) 
            {
                return;
            }
            if(event.getViewers().size() > 1)
            {
                player.sendMessage(Iris.formatMsg.format("<gold>Veuillez patienter, une transaction est en cours ..."));
                event.setCancelled(true);
                return;
            }
            if(economy.getSolde(player) < 1)
            {
                player.sendMessage(Iris.formatMsg.format("<light_purple>Tu crois que l'argent pousse sur les arbres !? va travailler ! tu n'as plus d'Emeraudes sur ton compte !"));
                event.setCancelled(true);
                return;
            }

            Inventory invChest = event.getView().getTopInventory();
            ArrayList<ChestTroc> chestTroc = new ArrayList();
                        
            int qte = 0;
            
            for(int slot = 0; slot < 9; slot++)
            {
                if (event.getInventory().getItem(slot) != null) 
                {
                    if (event.getInventory().getItem(slot).getItemMeta() != null) 
                    {
                        if (event.getInventory().getItem(slot).getItemMeta().hasLore()) 
                        {
                            for(ItemStack item : invChest.getContents())
                            {
                                if(item != null)
                                {
                                    if(item.getTypeId() == event.getInventory().getItem(slot).getTypeId() &&
                                            item.getData().getData() == event.getInventory().getItem(slot).getData().getData())
                                    {
                                        qte = qte + item.getAmount();
                                    }
                                }
                            }
                            ChestTroc itemChest = new ChestTroc(    
                                    player.getName(),
                                    event.getInventory().getItem(slot).getTypeId(),
                                    event.getInventory().getItem(slot).getData().getData(),
                                    qte);

                            chestTroc.add(itemChest);
                            qte = 0;
                        }
                    }
                }
                //else
                //{
                    //chestTroc.add(null);
                //}
            }
            ChestTrocs.put(player.getDisplayName(),chestTroc);
        }
        
        private boolean hasSignTroc(Block block)
        {
            if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
            {
                Sign sign = (Sign)block.getState();
                if (sign.getLine(0).contains("[TROC]"))
                {
                    return true;
                }
            }
            return false;
        }
        
        private boolean hasLampTroc(Block block)
        {
            //if (block.getType() == Material.REDSTONE_LAMP_ON || block.getType() == Material.REDSTONE_LAMP_OFF)
            if (block.getType() == Material.PRISMARINE || block.getType() == Material.SEA_LANTERN)
            {
                return true;
            }
            return false;
        }
        
        private void EditStatutTroc(Block block, String Etat, String PlayerName)
        {
            Sign sign = (Sign)block.getState();
            sign.setLine(1,formatMsg.format(Etat));
            sign.setLine(2,formatMsg.format(PlayerName));
            sign.update();
        }
        
        private void StatutLampTroc(Block block, int etat)
        {
            if (etat == 1)
            {
               // block.setType(Material.REDSTONE_LAMP_ON);
                block.setType(Material.SEA_LANTERN);
            }
            else
            {
                //block.setType(Material.REDSTONE_LAMP_OFF);
                block.setType(Material.PRISMARINE);
            }
        }
        
        private void getSignTroc(Block block, String Etat, String PlayerName)
        {          
            Block panx1 = block.getRelative(BlockFace.SOUTH);
            if (hasSignTroc(panx1))EditStatutTroc(panx1,Etat,PlayerName);
            Block panx2 = block.getRelative(BlockFace.NORTH);
            if (hasSignTroc(panx2))EditStatutTroc(panx2,Etat,PlayerName);
            Block panx3 = block.getRelative(BlockFace.WEST);
            if (hasSignTroc(panx3))EditStatutTroc(panx3,Etat,PlayerName);
            Block panx4 = block.getRelative(BlockFace.EAST);
            if (hasSignTroc(panx4))EditStatutTroc(panx4,Etat,PlayerName);
            Block panx5 = block.getRelative(BlockFace.NORTH_EAST);
            if (hasSignTroc(panx5))EditStatutTroc(panx5,Etat,PlayerName);
            Block panx6 = block.getRelative(BlockFace.SOUTH_EAST);
            if (hasSignTroc(panx6))EditStatutTroc(panx6,Etat,PlayerName);
            Block panx7 = block.getRelative(BlockFace.NORTH_WEST);
            if (hasSignTroc(panx7))EditStatutTroc(panx7,Etat,PlayerName);
            Block panx8 = block.getRelative(BlockFace.SOUTH_WEST);
            if (hasSignTroc(panx8))EditStatutTroc(panx8,Etat,PlayerName);
        }
        
        private void getLampTroc(Block block, int Etat)
        {          
            Block Lamp1 = block.getRelative(BlockFace.SOUTH);
            if (hasLampTroc(Lamp1))StatutLampTroc(Lamp1,Etat);
            Block Lamp2 = block.getRelative(BlockFace.NORTH);
            if (hasLampTroc(Lamp2))StatutLampTroc(Lamp2,Etat);
            Block Lamp3 = block.getRelative(BlockFace.EAST);
            if (hasLampTroc(Lamp3))StatutLampTroc(Lamp3,Etat);
            Block Lamp4 = block.getRelative(BlockFace.WEST);
            if (hasLampTroc(Lamp4))StatutLampTroc(Lamp4,Etat);
            
            Block Lamp5 = block.getRelative(-2,0,0);
            if (hasLampTroc(Lamp5) && block.getRelative(-1, 0, 0).getType() == Material.CHEST)StatutLampTroc(Lamp5,Etat);
            Block Lamp6 = block.getRelative(2,0,0);
            if (hasLampTroc(Lamp6) && block.getRelative(1, 0, 0).getType() == Material.CHEST)StatutLampTroc(Lamp6,Etat);
            Block Lamp7 = block.getRelative(0,0,-2);
            if (hasLampTroc(Lamp7) && block.getRelative(0, 0, -1).getType() == Material.CHEST)StatutLampTroc(Lamp7,Etat);
            Block Lamp8 = block.getRelative(0,0,2);
            if (hasLampTroc(Lamp8) && block.getRelative(0, 0, 1).getType() == Material.CHEST )StatutLampTroc(Lamp8,Etat);
        }
        
        @EventHandler
        public void InfoEtatTroc(InventoryCloseEvent event) 
        {
            if(!"TROC".contains(event.getInventory().getName()))
            {
                return;
            }
            
            Block block = null;
            if (event.getInventory().getSize() == 54)
            {
                DoubleChest c = (DoubleChest)(event.getInventory().getHolder());
                block = c.getLocation().getBlock();
            }
            else
            {
                Chest c = (Chest)(event.getInventory().getHolder());
                block = c.getLocation().getBlock();
            }    
            
            Inventory chest = event.getInventory();

            if(!"TROC".equals(chest.getName()))
            {
                return;
            }
            if (chest.getItem(0) == null) 
            {
                getSignTroc(block,"<dark_blue><gras>LIBRE","<dark_blue>--------------");
                getLampTroc(block,0);
                return;
            }
            if (chest.getItem(0).hasItemMeta() == false) 
            {
                getSignTroc(block,"<dark_blue><gras>LIBRE","<dark_blue>--------------");
                getLampTroc(block,0);
                return;
            }
            if (chest.getItem(0).getItemMeta().hasLore() == false) 
            {
                getSignTroc(block,"<dark_blue><gras>LIBRE","<dark_blue>--------------");
                getLampTroc(block,0);
                return;
            }
            getSignTroc(block,"<dark_red><gras>OUVERT","<italique>" + chest.getItem(0).getItemMeta().getDisplayName());
            getLampTroc(block,1);
        }
        
        @EventHandler
        public void RedstoneTroc(BlockRedstoneEvent e) 
        {
            //Iris.log.info(String.valueOf(e.getNewCurrent()));
            if ((e.getBlock().getType() == Material.REDSTONE_LAMP_OFF || e.getBlock().getType() == Material.REDSTONE_LAMP_ON) && 
                    (e.getBlock().getRelative(0, 0, 1).getType() == Material.CHEST ||  e.getBlock().getRelative(0, 0, -1).getType() == Material.CHEST ||
                    e.getBlock().getRelative(1, 0, 0).getType() == Material.CHEST || e.getBlock().getRelative(-1, 0, 0).getType() == Material.CHEST)) 
            {
                //Iris.log.info(String.valueOf(e.getNewCurrent()));
                e.setNewCurrent(5);
            }
        }
        
        @EventHandler()
        public final void onCloseInventory(InventoryCloseEvent event)
        {
            if(!"TROC".contains(event.getInventory().getName()))
            {
                return;
            }
                        
            if (event.getInventory().getItem(0) == null) 
            {
                return;
            }
            if (event.getInventory().getItem(0).hasItemMeta() == false) 
            {
                return;
            }
            if (event.getInventory().getItem(0).getItemMeta().hasLore() == false) 
            {
                return;
            }
                       
            Player player = (Player)event.getPlayer();
            if (event.getInventory().getItem(0).getItemMeta().getDisplayName().equals(player.getDisplayName()))
            {
                return;
            }
            Inventory invChest = event.getView().getTopInventory();
            String ticket = "<gras><gold>Total transaction :\n";
            double total = 0.00;
            int qte = 0;
            
            for(int slot = 0; slot < 9; slot++)
            {
                if (invChest.getItem(slot) != null) 
                {
                    if (event.getInventory().getItem(slot).getItemMeta() != null) 
                    {
                        if (event.getInventory().getItem(slot).getItemMeta().hasLore() == true) 
                        {
                            for(ItemStack itemStack : invChest.getContents())
                            {
                                if(itemStack != null)
                                {
                                    if(itemStack.getTypeId() == invChest.getItem(slot).getTypeId() &&
                                            itemStack.getData().getData() == invChest.getItem(slot).getData().getData())
                                    {
                                        qte = qte + itemStack.getAmount();
                                    }
                                }
                            }
                            if(qte > 0)
                            {
                                int size = ChestTrocs.get(player.getDisplayName()).size();                                
                                for(int x = 0; x < size; x++)
                                {
                                    if(event.getInventory().getItem(x) != null)
                                    {
                                        if(ChestTrocs.get(player.getDisplayName()).get(x) != null)
                                        {
                                            int type1 = ChestTrocs.get(player.getDisplayName()).get(x).getType();
                                            int type2 = event.getInventory().getItem(slot).getTypeId();
                                            int data1 = ChestTrocs.get(player.getDisplayName()).get(x).getData();
                                            int data2 = event.getInventory().getItem(slot).getData().getData();
                                            String p1 = ChestTrocs.get(player.getDisplayName()).get(x).getPlayername();
                                            String p2 = player.getName();
                                            
                                            if(event.getInventory().getItem(x).getItemMeta().hasLore())
                                            {    
                                                String[] args = event.getInventory().getItem(x).getItemMeta().getLore().get(0).split(" ");

                                                if( type1 == type2 && data1 == data2 && p1.equals(p2))
                                                {
                                                    double qteApres = qte;
                                                    double qteAvant = (double)ChestTrocs.get(player.getDisplayName()).get(x).getQte();


                                                    if(qteApres > qteAvant && args[0].contains("ACHAT")) //ACHAT
                                                    {
                                                        double qteAchat = qteApres - qteAvant;
                                                        double prix = qteAchat * Double.valueOf(args[5]);
                                                        prix = Math.round(prix * 100.0) / 100.0;
                                                        if(player.getPlayerListName().contains(event.getInventory().getItem(slot).getItemMeta().getDisplayName()))
                                                        {
                                                            economy.setTransaction(getPlayer(event.getInventory().getItem(slot).getItemMeta().getDisplayName()), player, prix, player.getLocation().getWorld().getName());
                                                        }
                                                        else
                                                        {
                                                            economy.setTransaction(getOfflinePlayer(event.getInventory().getItem(slot).getItemMeta().getDisplayName()), player, prix, player.getLocation().getWorld().getName());
                                                        }

                                                        ticket = ticket + "<gray>VENTE [" + type1 + ":" + data1 + "] " + qteAchat + " x " + args[5] + " = " + prix + "\n";
                                                        total = total + prix;
                                                    }

                                                    if(qteApres < qteAvant && args[0].contains("VENTE")) //VENTE
                                                    {
                                                        double qteVente = qteAvant - qteApres;
                                                        double prix = qteVente * Double.valueOf(args[5]);
                                                        prix = Math.round(prix * 100.0) / 100.0;
                                                        if(player.getPlayerListName().contains(event.getInventory().getItem(slot).getItemMeta().getDisplayName()))
                                                        {
                                                            economy.setTransaction(player,getPlayer(event.getInventory().getItem(slot).getItemMeta().getDisplayName()), prix, player.getLocation().getWorld().getName());
                                                        }
                                                        else
                                                        {
                                                            economy.setTransaction(player,getOfflinePlayer(event.getInventory().getItem(slot).getItemMeta().getDisplayName()), prix, player.getLocation().getWorld().getName());
                                                        }

                                                        ticket = ticket + "<gray>ACHAT [" + type1 + ":" + data1 + "] " + qteVente + " x " + args[5] + " = -" + prix + "\n";
                                                        total = total - prix;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                qte = 0;
            }
            
            if(ticket.contains("VENTE") || ticket.contains("ACHAT"))
            {
                ticket = ticket + "<gray>--------------------" + "\n";
                ticket = ticket + "<gras><gray>Total = " + total + " Emeraudes" + "\n";
                ticket = ticket + "<gray>--------------------" + "\n";
                player.sendMessage(formatMsg.format(ticket));
                           
                for(Player playerOnline : Bukkit.getServer().getOnlinePlayers())
                {
                    if(playerOnline.getDisplayName() == null ? event.getInventory().getItem(0).getItemMeta().getDisplayName() == null : playerOnline.getDisplayName().equals(event.getInventory().getItem(0).getItemMeta().getDisplayName()))
                    {
                        Player vendeur = getPlayer(event.getInventory().getItem(0).getItemMeta().getDisplayName());
                        vendeur.sendMessage(formatMsg.format("<gras><aqua><player> viens de vous acheter ou vendre des items pour un total de <yellow>" + -total, player));
                    }
                }
            }
        }
                        
        @EventHandler()
        public final void onInventoryClickEvent(InventoryClickEvent event)
        {
            if(!"TROC".contains(event.getInventory().getName()))
            {
                return;
            }
            
            if (event.isShiftClick())
            {
                event.setCancelled(true);
                return;
            }
            
            Player player = (Player)event.getWhoClicked();
            
            if(event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
            {
                if(event.getRawSlot() < 9)
                {
                    player.sendMessage(formatMsg.format("<gras><red>Action interdite ici"));
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);   
                    return;
                }
            }

            boolean itemCoffre = false;
            Inventory invChest = event.getView().getTopInventory();
            Inventory invPlayer = event.getView().getBottomInventory();
                                        
            if(invChest.getType() == InventoryType.CHEST && invPlayer.getType() == InventoryType.PLAYER)
            {  
            
                if (event.getInventory().getItem(0) == null) 
                {
                    return;
                }
                if (event.getInventory().getItem(0).hasItemMeta() == false) 
                {
                    return;
                }
                if (event.getInventory().getItem(0).getItemMeta().hasLore() == false) 
                {
                    return;
                }
                if (event.getCurrentItem() != null)
                {
                    if (event.getCurrentItem().hasItemMeta())
                    {
                        if (event.getCurrentItem().getItemMeta().getDisplayName() == null ? player.getName() == null : event.getCurrentItem().getItemMeta().getDisplayName().equals(player.getName()))
                        {
                            return;
                        }
                    }
                }
                
                if (event.getInventory().getItem(0).getItemMeta().getDisplayName() == null ? player.getName() == null : event.getInventory().getItem(0).getItemMeta().getDisplayName().equals(player.getName()))
                {
                    return;
                }
                
                if(event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.HOTBAR_SWAP)
                {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);   
                    return;
                }
                        
                //Les items sont pris pour un déplacement
                if(event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE || event.getAction() == InventoryAction.PICKUP_SOME)
                {                 
                    //Les items ont été pris dans le coffre
                    if(event.getRawSlot() == event.getSlot())
                    {
                        if(event.getRawSlot() < 9)
                        {
                            if(event.getInventory().getItem(event.getRawSlot()).hasItemMeta())
                            {
                                player.sendMessage(formatMsg.format("<gras><red>Ce bloc n'est pas a vendre"));
                                event.setCancelled(true);
                                event.setResult(Event.Result.DENY);   
                                return;
                            }
                        }
                        for(int i = 0; i < 9; i++) // on va vérifier que cette item est a vendre
                        {
                            try
                                {
                                if(event.getInventory().getItem(i) != null)
                                {
                                    if(event.getInventory().getItem(i).hasItemMeta()) //on vérifie si l'objet a comparer est un ItemMeta
                                    {
                                        String[] args = event.getInventory().getItem(i).getItemMeta().getLore().get(0).split(" ");
                                        if (args[0].contains("VENTE"))
                                        {
                                            if(event.getInventory().getItem(i).getTypeId() == event.getCurrentItem().getData().getItemTypeId() && 
                                                    event.getInventory().getItem(i).getData().getData() == event.getCurrentItem().getData().getData()) //Si item identique
                                            {
                                                itemCoffre = true;
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            catch(Exception ex){}
                        }
                        player.sendMessage(formatMsg.format("<gras><red>Ce bloc n'est pas a vendre"));
                        if(player.isOp())
                        {
                            return;
                        }
                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);   
                        return;
                    }
                    //Les items ont été pris sur le joueur
                    else
                    {
                        for(int i = 0; i < 9; i++) // on va vérifier que cette item a une offre d'achat
                        {  
                            if(event.getInventory().getItem(i) != null)
                            {
                                if(event.getInventory().getItem(i).hasItemMeta()) //on vérifie si l'objet a comparer est un ItemMeta
                                {
                                    String[] args = event.getInventory().getItem(i).getItemMeta().getLore().get(0).split(" ");
                                    if (args[0].contains("ACHAT"))
                                    {
                                        if(event.getInventory().getItem(i).getTypeId() == event.getCurrentItem().getData().getItemTypeId() && 
                                                event.getInventory().getItem(i).getData().getData() == event.getCurrentItem().getData().getData()) //Si item identique
                                        {                                         
                                            player.sendMessage(formatMsg.format("<yellow>Offre d'achat pour le prix de : <gras><green>" + args[5] + " emeraudes"));
                                            itemCoffre = false;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        player.sendMessage(formatMsg.format("<gras><red>Aucune offre pour ce bloc"));
                        itemCoffre = false;
                        return;
                    } 
                }
                //Les items sont posés dans un inventaire
                if(event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME)
                {                   
                    //Les items sont posés dans le coffre
                    if(event.getRawSlot() == event.getSlot())
                    {
                        if(event.getRawSlot() < 9)
                        {
                            player.sendMessage(formatMsg.format("<yellow>Vous ne devez pas poser les items sur la ligne du haut"));
                            event.setCancelled(true);
                            event.setResult(Event.Result.DENY);
                            return;
                        }
                            
                        for(int i = 0; i < 9; i++) // on va comparer l'objet troqué avec les objets à l'offre sur la premiere rangée
                        {
                            if(event.getInventory().getItem(i) != null)
                            {
                                if(event.getInventory().getItem(i).hasItemMeta()) //on vérifie si l'objet a comparer est un ItemMeta
                                {
                                    String[] args = event.getInventory().getItem(i).getItemMeta().getLore().get(0).split(" ");
                                    if (args[0].contains("ACHAT")) //objet à l'achat
                                    {
                                        if(event.getInventory().getItem(i).getTypeId() == event.getCursor().getData().getItemTypeId() && 
                                                event.getInventory().getItem(i).getData().getData() == event.getCursor().getData().getData()) //Si item identique
                                        {
                                            if(player.getPlayerListName().contains(event.getInventory().getItem(i).getItemMeta().getDisplayName()))
                                            {
                                                if(economy.getSolde(Bukkit.getPlayer(event.getInventory().getItem(i).getItemMeta().getDisplayName()))< 1)
                                                {
                                                    player.sendMessage(formatMsg.format("<yellow>Impossible de vendre a ce joueur, il n'a plus d'Emreraudes sur son compte"));
                                                    event.setCancelled(true);
                                                    event.setResult(Event.Result.DENY);
                                                    return;
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        player.sendMessage(formatMsg.format("<yellow>Aucune offre d'achat pour le bloc <gras><aqua>" + event.getCursor().getData().getItemTypeId() + ":" + event.getCursor().getData().getData()));
                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);
                    }
                    //Les items sont posés dans l'inventaire du joueur
                    else
                    {
                        if(itemCoffre == true) //Les items ont été déplacé du coffre au joueur
                        {
                            for(int i = 0; i < 9; i++) // on va comparer l'objet troqué avec les objets à l'offre sur la premiere rangée
                            {
                                if(event.getInventory().getItem(i) != null)
                                {
                                    if(event.getInventory().getItem(i).hasItemMeta()) //on vérifie si l'objet a comparer est un ItemMeta
                                    {
                                        String[] args = event.getInventory().getItem(i).getItemMeta().getLore().get(0).split(" ");
                                        if (args[0].contains("VENTE")) //objet à la vente
                                        {
                                            if(event.getInventory().getItem(i).getTypeId() == event.getCursor().getData().getItemTypeId() && 
                                                    event.getInventory().getItem(i).getData().getData() == event.getCursor().getData().getData()) //Si item identique
                                            {
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            player.sendMessage(formatMsg.format("<yellow>le bloc <aqua>" + event.getCursor().getData().getItemTypeId() + ":" + event.getCursor().getData().getData() + "<yellow> n'est pas a vendre"));
                            event.setCancelled(true);
                            event.setResult(Event.Result.DENY);
                        }
                        else //Les items ont juste été déplacé à l'intérieur de l'inventaire du joueur, on sort
                        {
                        }
                    } 
                }  
            }
        }
                
        @EventHandler
        public void onPlayerTrocPublic(PlayerInteractEntityEvent event) 
        {
            Player player = event.getPlayer();
            if (player.isOp())return;
            Entity entity = event.getRightClicked();
            if (entity instanceof ItemFrame) 
            {
                ItemFrame itemFrame = (ItemFrame)entity;
                if(itemFrame.getItem().hasItemMeta())
                {
                    String[] args = itemFrame.getItem().getItemMeta().getDisplayName().split(" ");         
                                            
                    if(args[0].contains("ACHAT") && player.getItemInHand().getTypeId() == itemFrame.getItem().getTypeId()
                            && player.getItemInHand().getData().getData() == itemFrame.getItem().getData().getData())
                    {
                        double qteAchat = player.getItemInHand().getAmount();
                        double prix = 0;
                        if(args.length == 4)
                        {
                            prix = qteAchat * Double.valueOf(args[2]);
                        }
                        else
                        {
                            prix = qteAchat * Double.valueOf(args[5]);
                        }
                        prix = Math.round(prix * 100.0) / 100.0;
                        
                        player.setItemInHand(null);
                        economy.setVersement(player, prix);
                        player.sendMessage(formatMsg.format("<gray>Vous avez recu <yellow>" + prix + " <gray>Emeraudes pour la vente de vos produits <a_gr> la communaut<e_ai>"));
                    }

                    if(args[0].contains("VENTE"))
                    {
                        double qteVente = 1;
                        double prix = 0;
                        if(args.length == 4)
                        {
                            prix = qteVente * Double.valueOf(args[2]);
                        }
                        else
                        {
                            prix = qteVente * Double.valueOf(args[5]);
                        }
                                               
                        prix = Math.round(prix * 100.0) / 100.0;
                        
                        if(player.getItemInHand().getTypeId() == 388)
                        {
                            if(prix > player.getItemInHand().getAmount())
                            {
                                player.sendMessage(formatMsg.format("<light_purple>Vous n'avez pas assez d'Emeraudes sur vous")); 
                            }
                            else
                            {
                                double roundprix = Math.round(prix);
                                if(roundprix < prix)
                                {
                                    roundprix = roundprix + 1;
                                }
                                
                                double solde = roundprix- prix;
                                
                                ItemStack is = player.getItemInHand();
                                if(is.getAmount() == roundprix) 
                                {
                                    player.setItemInHand(null);
                                }
                                else
                                {
                                    is.setAmount(is.getAmount()-(int) roundprix);
                                    player.setItemInHand(is);
                                }                           
                                
                                ItemStack itemsA = itemFrame.getItem(); //new ItemStack(itemFrame.getItem().getType());
                                ItemMeta meta = itemsA.getItemMeta();
                                List<String> lore = itemsA.getItemMeta().getLore();
                                String name = args[1].replace("[","");
                                name = name.replace("]","");
                                name = name.replace(":","");
                                name = name.replace("_"," ");
                                meta.setDisplayName(name);
                                meta.setLore(lore);
                                itemsA.setItemMeta(meta);
                                player.getInventory().addItem(itemsA);
                                player.updateInventory();
                                player.sendMessage(formatMsg.format("<gray>Vous avez <e_ai>t<e_ai> pr<e_ai>lev<e_ai> de <yellow>" + prix + " <gray>Emeraudes pour votre achat <a_gr> la communaut<e_ai>"));
                            }
                        }
                        else
                        {
                            if(economy.setPrelevement(player, prix) < 0)
                            {
                                economy.setVersement(player, prix);
                                player.sendMessage(formatMsg.format("<light_purple>Vous n'avez pas assez d'Emeraudes sur votre compte")); 
                            }
                            else
                            {
                                ItemStack itemsA = itemFrame.getItem(); //new ItemStack(itemFrame.getItem().getType());
                                ItemMeta meta = itemsA.getItemMeta();
                                List<String> lore = itemsA.getItemMeta().getLore();
                                String name = args[1].replace("[","");
                                name = name.replace("]","");
                                name = name.replace(":","");
                                name = name.replace("_"," ");
                                meta.setDisplayName(name);
                                meta.setLore(lore);
                                itemsA.setItemMeta(meta);
                                
                                player.getInventory().addItem(itemsA);
                                player.updateInventory();
                                player.sendMessage(formatMsg.format("<gray>Vous avez <e_ai>t<e_ai> pr<e_ai>lev<e_ai> de <yellow>" + prix + " <gray>Emeraudes pour votre achat <a_gr> la communaut<e_ai>>"));
                            }
                        }
                        
                    }
                }  
            }
        }
        
    }

