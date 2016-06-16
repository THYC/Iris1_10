package net.teraoctet.iris.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;


public class PlayersInventory 
{
    private static final ConfigFile conf = new ConfigFile();
    private final Map<Enchantment, Integer> enchantments = new HashMap();
    
    public void SaveInventory(Player player, String world)
    {
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
                
        PlayerInventory players_inventory = player.getInventory();
        String sql = "";

        ListIterator<ItemStack> it = players_inventory.iterator();

        while(it.hasNext())
        {
            ItemStack is = it.next();
            int id = it.nextIndex()-1;
            String enchant = "";
            String Name = "";
            
            if(is != null)
            {
                if(!is.getEnchantments().isEmpty())
                {
                    Map<Enchantment, Integer> enchantments = is.getEnchantments();
                    for(Enchantment e : enchantments.keySet())
                    {                       
                        enchant = enchant + e.getId() + ":" + is.getEnchantmentLevel(e) + ";";
                    }
                }
                else
                {
                    enchant = "";
                }
                if(is.hasItemMeta())
                {
                    Name = is.getItemMeta().getDisplayName();
                }
                else
                {
                    Name = "";
                }
                
                sql = "REPLACE INTO  irisinventory SET "
                        + "id_itemsPlace = " + id + ", "
                        + "playerUUID = '" + player.getUniqueId().toString() + "', "
                        + "world = '" + world + "', "
                        + "items_type = " + is.getTypeId() + ", "
                        + "items_durability = " + is.getDurability() + ", "
                        + "items_amount = " + is.getAmount() + ", "
                        + "items_name = '" + Name + "', "
                        + "items_enchantment = '" + enchant + "'";
            }
            else
            {
                sql = "REPLACE INTO  irisinventory SET "
                        + "id_itemsPlace = " + id + ", "
                        + "playerUUID = '" + player.getUniqueId().toString() + "', "
                        + "world = '" + world + "', "
                        + "items_type = 0, "
                        + "items_durability = 0, "
                        + "items_amount = 0, "
                        + "items_name = '', "
                        + "items_enchantment = ''";
            }

            ConMySQL.execBatch(sql);
        }
        saveEquipment(player,world);
        SavePlayer(player,world);
    }
    
    private void saveEquipment(Player player, String world)
    {
        ItemStack Casque = player.getEquipment().getHelmet();
        SaveIS(player,world,Casque,103);
        ItemStack Poitrine = player.getEquipment().getChestplate();
        SaveIS(player,world,Poitrine,102);
        ItemStack Pantalon = player.getEquipment().getLeggings();
        SaveIS(player,world,Pantalon,101);
        ItemStack Botte = player.getEquipment().getBoots();
        SaveIS(player,world,Botte,100);       
    }
    
    private void SaveIS(Player player, String world, ItemStack is, int id)
    {
        String sql = "";
        String enchant = "";
        String Name = "";
            
        if(is != null)
        {            
            if(!is.getEnchantments().isEmpty())
            {
                @SuppressWarnings("LocalVariableHidesMemberVariable")
                Map<Enchantment, Integer> enchantments = is.getEnchantments();
                for(Enchantment e : enchantments.keySet())
                {                       
                    enchant = enchant + e.getId() + ":" + is.getEnchantmentLevel(e) + ";";
                }
            }
            else
            {
                enchant = "";
            }
            
            if(is.hasItemMeta())
            {
                Name = is.getItemMeta().getDisplayName();
            }
            else
            {
                Name = "";
            }
            
            sql = "REPLACE INTO  irisinventory SET "
                    + "id_itemsPlace = " + id + ", "
                    + "playerUUID = '" + player.getUniqueId().toString() + "', "
                    + "world = '" + world + "', "
                    + "items_type = " + is.getTypeId() + ", "
                    + "items_durability = " + is.getDurability() + ", "
                    + "items_amount = " + is.getAmount() + ", "
                    + "items_name = '" + Name + "', "
                    + "items_enchantment = '" + enchant + "'";
        }
        else
            {
                sql = "REPLACE INTO  irisinventory SET "
                        + "id_itemsPlace = " + id + ", "
                        + "playerUUID = '" + player.getUniqueId().toString() + "', "
                        + "world = '" + world + "', "
                        + "items_type = 0, "
                        + "items_durability = 0, "
                        + "items_amount = 0, "
                        + "items_name = '', "
                        + "items_enchantment = ''";
            }
        ConMySQL.executeInsert(sql);    
    }
    
    public void LoadInventory(Player player, String world) throws SQLException
    {
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
        
        PlayerInventory players_inventory = player.getInventory();

        String sql = "SELECT * FROM irisinventory WHERE playerUUID = '" 
                + player.getUniqueId().toString() + "' AND world = '"
                + world + "';";
        ResultSet rs = ConMySQL.executeSelect(sql);
        if (rs != null) 
        {
            players_inventory.clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            
            ItemStack is;                    
            

            try
            { 
                while (rs.next())
                {
                    int item = rs.getInt("items_type");
                    int slot = rs.getInt("id_itemsPlace");
                    is = new ItemStack(0);

                    is.setTypeId(item);
                    is.setDurability((short) rs.getInt("items_durability"));
                    is.setAmount(rs.getInt("items_amount"));
                    
                    
                    if (!"0".equals(rs.getString("items_type")))
                    {
                        if(!"null".contains(rs.getString("items_name")) && !"".equals(rs.getString("items_name")) && rs.getString("items_name")!= null)
                        {
                            ItemMeta m = is.getItemMeta();
                            m.setDisplayName(rs.getString("items_name"));
                            is.setItemMeta(m);
                        }
                                                
                        if (rs.getString("items_enchantment").contains(":"))
                        {
                            this.enchantments.clear();
                            getEnchantments(rs.getString("items_enchantment"));
                            is.addUnsafeEnchantments(this.enchantments);
                        }
                        switch(slot)
                        {
                            case 100:
                                players_inventory.setBoots(is);
                                break;
                            case 101:
                                players_inventory.setLeggings(is);
                                break;
                            case 102:
                                players_inventory.setChestplate(is);
                                break;
                            case 103:
                                players_inventory.setHelmet(is);
                                break;
                            default:
                                players_inventory.setItem(slot, is);
                        }
                    }
                }
            } 
            catch (SQLException ex) 
            {
                Iris.log.severe(ex.getMessage());
            }
        }
        LoadPlayer(player,world);
    }
    
    public Boolean PlayerExist(Player player)
    {
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
        
        String sql = "Select * from irisplayer where playerUUID = '" + player.getUniqueId().toString() + "';"; 
        ResultSet rs  = ConMySQL.executeSelect(sql);
        if (rs != null)
        {
            return true;
        }        
        return false;
    }
    
    public void newPlayer(Player player)
    {
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
        
        String sql = "INSERT INTO irisplayer (playerUUID, playerName, playerLevel, playerIP, money, id_metier)" + 
                "VALUES ('" + player.getUniqueId().toString() + "','" + player.getDisplayName() + "',0,'" +  player.getAddress()   + "',100,0); ";
        ConMySQL.executeInsert(sql);
    }
    
    public void SavePlayer(Player player, String world)
    {
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
        
        String sql = "";
        sql = "REPLACE INTO  irisplayerworld SET "
                + "playerUUID = '" + player.getUniqueId().toString() + "', "
                + "world = '" + world + "', "
                + "health = " + player.getHealth() + ", "
                + "hunger = " + player.getFoodLevel() + ", "
                + "saturation = " + player.getSaturation() + ", "
                + "experience = " + player.getExp() + ", "
                + "totalexperience = " + player.getExp() + ", "
                + "plevel = " + player.getLevel() + ";";

        ConMySQL.execBatch(sql);  
    }
    
    public void LoadPlayer(Player player, String world) throws SQLException
    {
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {}
                
        try
        {
            String sql = "SELECT * FROM irisplayerworld WHERE playerUUID = '" 
                    + player.getUniqueId().toString() + "' AND world = '"
                    + world + "';";
            ResultSet rs = ConMySQL.executeSelect(sql);
            if (rs != null) 
            {
                rs.first();
                player.setHealth(Double.valueOf(rs.getString("health")));
                player.setFoodLevel(rs.getInt("hunger"));
                player.setSaturation(rs.getFloat("saturation"));
                player.setExp(rs.getFloat("experience"));
                player.setTotalExperience(rs.getInt("totalexperience"));
                player.setLevel(rs.getInt("plevel"));
            }
        }
        catch(SQLException | NumberFormatException ex){}
    }
    
    public ItemStack itemStack(ItemStack item, String _enchantements)
    {
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        String[] E1 = _enchantements.split(";");
        
        Enchantment enchantment; 
        
        for(String E2 : E1)
        {
            try
            {
                String[] enchant = E2.split(":");
                
                int idEnchant = Integer.valueOf(enchant[0]);
                int Level = Integer.valueOf(enchant[1]);
               
                enchantment = Enchantment.getById(idEnchant);
                
                if(enchantment != null)
                {
                    //enchantments.put(Enchantment.getById(idEnchant), Level);
                }    
                item.addUnsafeEnchantment(enchantment, Level);
            }
            catch(Exception ex)
            {
                Iris.log.info("Erreur : " + ex.getMessage());
            }
        }
        return item;        
    }
    
    private void getEnchantments(String enchantmentString)
    {
        if (!"0".equals(enchantmentString))
        {
            String[] enchantments = enchantmentString.split(";");
            for (String enchantment : enchantments)
            {
                String[] parts = enchantment.split(":");
                Enchantment e;
                try
                {
                  e = Enchantment.getById(Integer.parseInt(parts[0]));
                }
                catch (NumberFormatException ex)
                {
                  e = Enchantment.getByName(parts[0]);
                }
                
                int level = Integer.parseInt(parts[1]);
                if (e != null) {
                  this.enchantments.put(e, Integer.valueOf(level));
                }
            }
        }
    }
}
