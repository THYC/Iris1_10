package net.teraoctet.iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile extends YamlConfiguration
{         
    public static File dataFolder;
    File configFile;
    FileConfiguration configYAML;
    File configTXT;
    
    /**
     * Création d'un fichier de configuration vide dans une
     * dossier autre que le dossier DataFolder
     * 
     * @param fileName nom du fichier à creer
     * @param folderName nom du dossier à creer
     * @return renvoie True si le fichier n'éxistait pas
     */
    public boolean paramFile (String fileName, String folderName) 
    {
        File folder = new File(folderName);
        File file = new File(folderName + "//" + fileName);

        if (!folder.exists())
        { 
            try 
            {
                folder.mkdirs();
            } 
            catch (Exception e){}
        }

        if (!file.exists())
        {
                try
                {
                        file.createNewFile();
                        return true;
                }
                catch (IOException e){}
        }
        return false;
    }   
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [String]
     * @param valueDefault valeur du paramètre [String]
     * @return renvoie True
     */
    public boolean setStringYAML(String folder, String fileName, String node, String valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        fileConf.set(node, valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
            return false;
        }
        return true;
    }
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [String]
     * @param valueDefault valeur du paramètre [Double]
     * @return renvoie True
     */
    public boolean setDoubleYAML(String folder, String fileName, String node, double valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        fileConf.set(node, valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return true;
    }
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [int]
     * @param valueDefault valeur du paramètre [Double]
     * @return renvoie True
     */
    public boolean setIntYAML(String folder, String fileName, String node, int valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        fileConf.set(node, valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return true;
    }
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [int]
     * @param valueDefault valeur du paramètre [Long]
     * @return renvoie True
     */
    public boolean setLongYAML(String folder, String fileName, String node, long valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        fileConf.set(node, valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return true;
    }
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [String]
     * @param valueDefault valeur du paramètre [Date]
     * @return renvoie True
     */
    public boolean setDateYAML(String folder, String fileName, String node, Date valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        Long date = valueDefault.getTime();
        fileConf.set(node, date);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return true;
    }
    
    /**
     * Enregistre une valeur dans un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à enregister [String]
     * @param valueDefault valeur du paramètre [boolean]
     * @return renvoie True
     */
    public boolean setBooleanYAML(String folder, String fileName, String node, boolean valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        fileConf.set(node, valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return true;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [String]
     * @return retourne la valeur [String]
     */    
    public String getStringYAML(String folder, String fileName, String node, String valueDefault)
    {
        File file = new File(dataFolder + File.separator +  folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        valueDefault = fileConf.getString(node,valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return valueDefault;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [double]
     * @return retourne la valeur [double]
     */    
    public Double getDoubleYAML(String folder, String fileName, String node, double valueDefault)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        valueDefault = fileConf.getDouble(node,valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return valueDefault;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [long]
     * @return retourne la valeur [double]
     */    
    public long getLongYAML(String folder, String fileName, String node, long valueDefault)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        valueDefault = fileConf.getLong(node,valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return valueDefault;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [long]
     * @return retourne la valeur [double]
     */    
    public Date getDateYAML(String folder, String fileName, String node, Date valueDefault)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        Date d = new Date(fileConf.getLong(node,valueDefault.getTime()));
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return d;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [int]
     * @return retourne la valeur [int]
     */    
    public int getIntYAML(String folder, String fileName, String node, int valueDefault)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        valueDefault = fileConf.getInt(node,valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return valueDefault;
    }
    
    /**
     * Retourne une valeur d'un paramètre YAML
     * dossier autre que le dossier DataFolder
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param node nom du paramètre à retourner [String]
     * @param valueDefault valeur par default du paramètre [boolean]
     * @return retourne la valeur [boolean]
     */    
    public boolean getBooleanYAML(String folder, String fileName, String node, boolean valueDefault)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName); 
        FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
        valueDefault = fileConf.getBoolean(node,valueDefault);
        try 
        {
            fileConf.save(file);
        } 
        catch (IOException e) 
        {
        }
        return valueDefault;
    }
    
    /**
     * Obtient l'existance d'un paramètre YAML
     * 
     * @param folder chemin à partir du DataFolder (ex: "userdata") [String]
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à tester [String]
     * 
     * @return retourne True si existe
     */
    public Boolean IsConfigYAML(String folder, String fileName, String param)
    {
        File file = new File(dataFolder + File.separator + folder + File.separator + fileName);
        Boolean isConfig = false;
        if (file.exists()) 
        {
            FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            isConfig = fileConf.isConfigurationSection(param);
        }
        return isConfig;
    }
    
    /**
     * Retourne un ConfigFileuration Section
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param nom du paramètre [String]
     * 
     * @return retourne le paramètre
     */
    public ConfigurationSection getConfigurationSection(String fileName, String param)
    {
        configFile = new File(dataFolder, fileName);
        ConfigurationSection ConfigSection = null;
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                ConfigSection = configYAML.getConfigurationSection(param);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
            }
        }
        return ConfigSection;
    }
    
    /**
     * Retourne une Section
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param nom du paramètre [String]
     * 
     * @return retourne le paramètre
     */
    public Set<String> getKeysYAML(String fileName, String param)
    {
        configFile = new File(dataFolder, fileName);
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try {   
                configYAML.load(configFile);
            } catch (IOException | InvalidConfigurationException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(IsConfigYAML(fileName,param))
            {
                Set<String> keys = configYAML.getConfigurationSection(param).getKeys(false);
                return keys;
            }
        }
        return null;
    }
    
    /**
     * Obtient l'existance d'un paramètre YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à tester [String]
     * 
     * @return retourne True si existe
     */
    public Boolean IsConfigYAML(String fileName, String param)
    {
        configFile = new File(dataFolder, fileName);
        Boolean isConfig = false;
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                isConfig = configYAML.isConfigurationSection(param);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
            }
        }
        return isConfig;
    }
        
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public String getStringYAML(String fileName, String node)
    {
        File file = new File(dataFolder, fileName);
        String value = null;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getString(node);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public String getStringYAML(String fileName, String node, String valueDefault)
    { 
        File file = new File(dataFolder, fileName);
        String value = null;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getString(node,valueDefault);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public int getIntYAML(String fileName, String node)
    {
        File file = new File(dataFolder, fileName);
        int value = 0;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getInt(node);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre à obtenir [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public int getIntYAML(String fileName, String node, int valueDefault)
    {
        File file = new File(dataFolder, fileName);
        int value = 0;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getInt(node,valueDefault);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre à obtenir [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public double getDoubleYAML(String fileName, String node, double valueDefault)
    {
        File file = new File(dataFolder, fileName);
        double value = 0;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getDouble(node,valueDefault);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre à obtenir [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public BigDecimal getBigDecimalYAML(String fileName, String node, long valueDefault)
    {
        File file = new File(dataFolder, fileName);
        BigDecimal value = BigDecimal.ZERO;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = BigDecimal.valueOf(fileConf.getLong(node,valueDefault));
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre à obtenir [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public Boolean getBooleanYAML(String fileName, String node, Boolean valueDefault)
    {
        File file = new File(dataFolder, fileName);
        boolean value = valueDefault;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getBoolean(node,valueDefault);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre à obtenir [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne la valeur du paramètre [Long]
     */
    public Long getLongYAML(String fileName, String node, Long valueDefault)
    {
        File file = new File(dataFolder, fileName);
        long value = 0;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            value = fileConf.getLong(node,valueDefault);
        }
        return value;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre 'List' à obtenir [String]
     * 
     * @return retourne la valeur du node [List]
     */
    public List<String> getListYAML(String fileName, String node)
    {
        File file = new File(dataFolder, fileName);
        List<String>list = null;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            list = fileConf.getStringList(node);
        }
        return list;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre 'List' à obtenir [String]
     * 
     * @return retourne la valeur du node [List]
     */
    public List<Long> getListLongYAML(String fileName, String node)
    {
        File file = new File(dataFolder, fileName);
        List<Long>list = null;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            list = fileConf.getLongList(node);
        }
        return list;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param node paramètre 'List' à obtenir [String]
     * 
     * @return retourne la valeur du node [List]
     */
    public List<Integer> getListIntYAML(String fileName, String node)
    {
        File file = new File(dataFolder, fileName);
        List<Integer>list = null;
        if (file.exists()) 
        { 
            YamlConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            list = fileConf.getIntegerList(node);
        }
        return list;
    }
    
    /**
     * Obtient le paramètre  d'un fichier configuration TXT
     * 
     * @param fileName nom du fichier YAML [String]
     * 
     * @return retourne la valeur du paramètre [String]
     */
    public String getConfigTXT(String fileName)
    {
        String txt = "";
        configFile = new File(dataFolder, fileName);
        
        if (configFile.exists()) 
        { 
            try 
            {
                InputStream ips = new FileInputStream(configFile); 
                InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String ligne;
		while ((ligne=br.readLine())!= null)
                {
                    System.out.println(ligne);
                    txt+=ligne+"\n";
		}
		br.close(); 
            } 
            catch (IOException e) 
            {
            }
        }
        return txt;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [BigDecimal]
     * @param valueDefault paramètre par default
     * 
     * @return retourne true
     */
    public boolean setBigDecimalYAML(String fileName, String param, BigDecimal valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [Int]
     * @param valueDefault paramètre par default
     * 
     * @return retourne true
     */
    public boolean setIntYAML(String fileName, String param, int valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [Long]
     * @param valueDefault paramètre par default
     * 
     * @return retourne true
     */
    public boolean setLongYAML(String fileName, String param, Long valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [double]
     * @param valueDefault paramètre par default
     * 
     * @return retourne true
     */
    public boolean setDoubleYAML(String fileName, String param, double valueDefault)
    {
        configFile = new File(dataFolder, fileName);       
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [String]
     * @param valueDefault paramètre par default
     * 
     * @return retourne true
     */
    public boolean setStringYAML(String fileName, String param, String valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [String]
     * @param valueDefault valeur
     * 
     * @return
     */
    public boolean setBooleanYAML(String fileName, String param, boolean valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
     /**
     * modifie le paramètre  d'un fichier configuration YAML
     * 
     * @param fileName nom du fichier YAML [String]
     * @param param paramètre à modifier [String]
     * @param valueDefault valeur
     * 
     * @return
     */
    public boolean setListYAML(String fileName, String param, List valueDefault)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, valueDefault);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean delNodeYAML(String fileName, String param)
    {
        configFile = new File(dataFolder, fileName);
                
        if (configFile.exists()) 
        {
            configYAML = new YamlConfiguration(); 
            try 
            {
                configYAML.load(configFile);
                configYAML.set(param, null);
                configYAML.save(configFile);
            } 
            catch (IOException | InvalidConfigurationException e) 
            {
                return false;
            }
        }
        return true;
    }
    
    private void copy(InputStream in, File file) 
    {
        try 
        {
            try (OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len;
                while((len=in.read(buf))>0)
                {
                    out.write(buf,0,len);
                }
            }
        in.close();
        }
        catch (IOException e) 
        {
        }
    }
    
    public InputStream getResource(String filename) 
    {
        if (filename == null) 
        {
            throw new IllegalArgumentException("Filename cannot be null");
        }
   
        try 
        {
            URL url = Iris.class.getClassLoader().getResource(filename);
            if (url == null) 
            {
                return null;
            }
   
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } 
        catch (IOException ex) 
        {
               return null;
        }
    }
    
    /**
     * Copie d'un fichier de configuration YAML
     * 
     * @param fileName nom du fichier YAML à copier
     */
    public void ConfigFileYAML (String fileName) 
    {
        configFile = new File(dataFolder, fileName);        
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(getResource(fileName), configFile);
        }
        configYAML = new YamlConfiguration();       
        loadYamls();
    } 
    
    /**
     * Copie d'un fichier de configuration YAML
     * 
     * @param fileName nom du fichier YAML à copier
     * @param fileNameCopy nom du fichier après copie
     * @param folder nom du dossier
     */
    public void ConfigFileYAML (String fileName, String fileNameCopy, String folder) 
    {
        configFile = new File(dataFolder + "//" + folder, fileNameCopy);        
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(getResource(fileName), configFile);
        }
        configYAML = new YamlConfiguration();       
        loadYamls();
    } 
    
    /**
     * Création d'un fichier de configuration TXT
     * 
     * @param fileName nom du fichier à creer
     */
    public void ConfigFileTXT (String fileName) 
    {
        configFile = new File(dataFolder, fileName); 
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(getResource(fileName), configFile);
        }
        configTXT = new File(fileName);
    } 
    
    public void ConfigFileTXT (String folder, String fileName) 
    {
        configFile = new File(dataFolder + File.separator +  folder, fileName); 
        if(!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            copy(getResource(fileName), configFile);
        }
        configTXT = new File(fileName);
    } 
        
    private void saveYamls() 
    {
        try 
        {
            configYAML.save(configFile);
        } 
        catch (IOException e) 
        {
        }
    }
    
    private void loadYamls() 
    {
        try 
        {
            configYAML.load(configFile);        
        } 
        catch (IOException | InvalidConfigurationException e) 
        {
        }
    }
}
