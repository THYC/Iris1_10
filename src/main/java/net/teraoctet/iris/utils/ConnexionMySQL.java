package net.teraoctet.iris.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import org.bukkit.Bukkit;

public class ConnexionMySQL 
{
    private static String dbURL = "";
    private static String user = "";
    private static String password = "";
    private static Connection dbConnect = null;
    public Statement dbStatement = null;

    private static Boolean ConnexionMySQL() 
    {
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
 
        try 
        {
 
            dbConnect = DriverManager.getConnection(dbURL, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
        /*
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            dbConnect = DriverManager.getConnection(dbURL, user, password);
            Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Connection MYSQL OK"));
            return true;
        } 
        catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) 
        {
            Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Connection MYSQL ERREUR"));
            return false;
        }*/
    }
    
    public static Boolean getInstance(String url, String User, String Password)
    {
        if(dbConnect == null)
        {
            dbURL = url + "?autoReconnect=true";
            user= User;
            password = Password;
            
            return ConnexionMySQL();
        }
        return true;   
    }   
    
    public Boolean createSGBD() throws IOException
    {
        try 
        {
            PreparedStatement stmt = null;
            String sql = "";
            
            ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost/iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","thyc67"));
            
                          
            if (!dbConnect.getMetaData().getTables(null, null, "irisplayer", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisplayer" +
                "(" +
                "    playerUUID          CHAR(50)   	NOT NULL," +
                "    playerName     	CHAR(50)   	NOT NULL," +
                "    adresseIP           CHAR(50)," +
                "    playerLevel    	INT	    	NOT NULL," +
                "    playerIP      	CHAR(30)," +
                "    money               DOUBLE DEFAULT 5," +
                "    id_metier           INT DEFAULT 0   NOT NULL, " +
                "    password            CHAR(50)," +
                "    blockBreak          INT DEFAULT 0   NOT NULL," +
                "    blockBuild          INT DEFAULT 0   NOT NULL," +
                "    woodBreak           INT DEFAULT 0   NOT NULL," +
                "    woodPlant           INT DEFAULT 0   NOT NULL," +
                "    CulturePlant        INT DEFAULT 0   NOT NULL," +
                "    eleveur             INT DEFAULT 0   NOT NULL," +
                "    PRIMARY KEY (playerUUID)," +
                "    UNIQUE KEY (playerUUID)" +
                "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisplayer ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irismouchard", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irismouchard" +
                    "(" +
                    "    id_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "    playerName     	CHAR(50)   	NOT NULL," +
                    "    adresseIP           CHAR(50)," +
                    "    blockBreak          INT DEFAULT 0   NOT NULL," +
                    "    location            CHAR(255)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irismouchard ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irismessagename", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irismessagename" +
                    "(" +
                    "    messagename         CHAR(50)        NOT NULL," +
                    "    groupeperm      	CHAR(50) DEFAULT 'default'," +
                    "    actif               INT DEFAULT 1   NOT NULL," +
                    "    PRIMARY KEY (messagename)," +
                    "    UNIQUE KEY (messagename)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irismessagename ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irislocation", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irislocation" +
                    "(" +
                    "    id_location		INT NOT NULL AUTO_INCREMENT," +
                    "    locationName	CHAR(30)    NOT NULL," +
                    "    playerUUID          CHAR(50) DEFAULT 'NC'," +
                    "    world		CHAR(30)	NOT NULL," +
                    "    X			DOUBLE		NOT NULL," +
                    "    Y			DOUBLE		NOT NULL," +
                    "    Z			DOUBLE		NOT NULL," +
                    "    id_type		INT		NOT NULL," +
                    "    PRIMARY KEY (id_location)" +
                    "    )ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irislocation ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisprotect", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisprotect" +
                    "(" +
                    "    pass        	CHAR(30)    NOT NULL," +
                    "    playerUUID          CHAR(50) DEFAULT 'NC'," +
                    "    world		CHAR(30)	NOT NULL," +
                    "    X			DOUBLE		NOT NULL," +
                    "    Y			DOUBLE		NOT NULL," +
                    "    Z			DOUBLE		NOT NULL," +
                    "    id_type		INT		NOT NULL," +
                    "    PRIMARY KEY (X,Y,Z,world)" +
                    "    )ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisprotect ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisplayerworld", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisplayerworld" +
                    "(" +
                    "	playerUUID		CHAR(50)                NOT NULL," +
                    "	world			CHAR(30)                NOT NULL," +
                    "        health                  DOUBLE                  ," +
                    "        hunger                  INT                     ," +
                    "        saturation              FLOAT                   ," +
                    "        experience              FLOAT                   ," +
                    "        totalexperience         INT                     ," +
                    "        plevel                  INT                     ," +
                    "	PRIMARY KEY (playerUUID, world)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisplayerworld ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisinventory", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisinventory" +
                    "(" +
                    "	id_itemsPlace		INT                     NOT NULL," +
                    "	playerUUID		CHAR(50)                NOT NULL," +
                    "        world			CHAR(30)                NOT NULL," +
                    "	items_type		INT                     ," +
                    "        items_durability        INT                     ," +
                    "	items_amount		INT                     ," +
                    "        items_enchantment       CHAR(255)               ," +
                    "	PRIMARY KEY (id_itemsPlace, playerUUID, world)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisinventory ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisworld", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisworld" +
                    "(" +
                    "	world                   CHAR(30)                NOT NULL," +
                    "	groupWorld              INT			NOT NULL," +
                    "	PRIMARY KEY (world)," +
                    "        UNIQUE KEY (world)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisworld ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisportal", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisportal" +
                    "(" +
                    "	portalName		CHAR(30)                NOT NULL," +
                    "	world			CHAR(30)                NOT NULL," +
                    "	X1			INT			NOT NULL," +
                    "	Y1			INT			NOT NULL," +
                    "	Z1			INT			NOT NULL," +
                    "	X2			INT			NOT NULL," +
                    "	Y2			INT			NOT NULL," +
                    "	Z2			INT			NOT NULL," +
                    "	toworld			CHAR(30)," +
                    "	toX1			INT," +
                    "	toY1			INT," +
                    "	toZ1			INT," +
                    "	bookName		CHAR(30)," +
                    "	message			CHAR(255)," +
                    "        mode                    INT DEFAULT 0," +
                    "	PRIMARY KEY (portalName)," +
                    "	UNIQUE KEY (portalName)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisportal ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisparcelle", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisparcelle" +
                    "(" +
                    "	parcelleName		CHAR(30)                NOT NULL," +
                    "        parent                  CHAR(30)," +
                    "	world			CHAR(30)                NOT NULL," +
                    "	X1			INT			NOT NULL," +
                    "	Y1			INT			NOT NULL," +
                    "	Z1			INT			NOT NULL," +
                    "	X2			INT			NOT NULL," +
                    "	Y2			INT			NOT NULL," +
                    "	Z2			INT			NOT NULL," +
                    "	jail                    INT DEFAULT 0," +
                    "        noEnter                 INT DEFAULT 0," +
                    "        noFly                   INT DEFAULT 0," +
                    "        noBuild                 INT DEFAULT 1," +
                    "        noBreak                 INT DEFAULT 1," +
                    "        noTeleport              INT DEFAULT 0," +
                    "        noInteract              INT DEFAULT 0," +
                    "        noFire                  INT DEFAULT 1," +
                    "	message			CHAR(255)," +
                    "        mode                    INT DEFAULT 0," +
                    "        mobspawn                INT DEFAULT 1," +
                    "        mobkill                 INT DEFAULT 0," +
                    "	PRIMARY KEY (parcelleName)," +
                    "	UNIQUE KEY (parcelleName)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisparcelle ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irisparcellemember", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irisparcellemember" +
                    "(" +
                    "        playerUUID          CHAR(50)                    NOT NULL," +
                    "        playerName          CHAR(50)                    NOT NULL," +
                    "        parcelleName        CHAR(30)                    NOT NULL," +
                    "        typeMember          INT DEFAULT 0               NOT NULL," +
                    "        PRIMARY KEY (playerUUID, parcelleName)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irisparcellemember ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irishorde", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irishorde" +
                    "(" +
                    "        hordeName           CHAR(30)                    NOT NULL," +
                    "        world               CHAR(50)                    NOT NULL," +
                    "        X1                  INT                         NOT NULL," +
                    "	Y1                  INT                         NOT NULL," +
                    "	Z1                  INT                         NOT NULL," +
                    "        kills               INT DEFAULT 0               NOT NULL," +
                    "        dead                INT DEFAULT 0               NOT NULL," +
                    "        PRIMARY KEY (hordeName)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irishorde ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irishordemember", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irishordemember" +
                    "(" +
                    "        playerUUID          CHAR(50)                    NOT NULL," +
                    "        playerName          CHAR(50)                    NOT NULL," +
                    "        hordeName           CHAR(30)                    NOT NULL," +
                    "        typeMember          INT DEFAULT 0               NOT NULL," +
                    "        power               INT(10) DEFAULT 10          NOT NULL," +
                    "        PRIMARY KEY (playerUUID)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irishordemember ok");
            }
            
            if (!dbConnect.getMetaData().getTables(null, null, "irischunk", null).next())
            {
                sql = "CREATE TABLE IF NOT EXISTS irischunk" +
                    "(" +
                    "        playerUUID              CHAR(50) DEFAULT 'NC'   NOT NULL," +
                    "        hordename               CHAR(50) DEFAULT 'NC'   NOT NULL," +
                    "        world			CHAR(30)                NOT NULL," +
                    "	X1			INT                     NOT NULL," +
                    "	Z1			INT                     NOT NULL," +
                    "	typeMember              INT DEFAULT 0," +
                    "        jail                    INT DEFAULT 0," +
                    "        noBuild                 INT DEFAULT 1," +
                    "        noBreak                 INT DEFAULT 1," +
                    "        noInteract              INT DEFAULT 0," +
                    "        noFire                  INT DEFAULT 1," +
                    "        noPVP                   INT DEFAULT 0," +
                    "        noKillAnimal            INT DEFAULT 1," +
                    "	message			CHAR(255)," +
                    "        PRIMARY KEY (hordename, world, X1, Z1)" +
                    "	)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

                stmt = dbConnect.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                log.info("[Iris]MySQL creation table irischunk ok");
            }
            
            return true;
        } 
        catch (SQLException ex) 
        {
            Iris.log.severe("[Iris] Impossible de se connecter au serveur MySQL");
            PluginUtil.disable(Bukkit.getPluginManager().getPlugin("Iris"));
        }
        return false;
    }
    
    public void execBatch(String sql) 
    {
        try {
            dbStatement.addBatch(sql);
            dbStatement.executeBatch();
        } 
        catch (SQLException ex) 
        {
            Iris.log.severe(ex.getMessage());
        }
    }
         
    public boolean executeInsert(String query) {
        try 
        {
            dbStatement = dbConnect.createStatement();
            return dbStatement.execute(query);
        } 
        catch (SQLException ex) 
        {
            Iris.log.severe(ex.getMessage());
            return false;
        }
    }
 
    public int executeUpdate(String query) 
    {
        try 
        {
            dbStatement = dbConnect.createStatement();
            return dbStatement.executeUpdate(query);
        } 
        catch (SQLException e) 
        {
            System.err.println(e.getSQLState());
            System.err.println(e.getMessage());
            return -1;
        }
    }
 
    public ResultSet executeSelect(String query) 
    {
        try 
        {
            dbStatement = dbConnect.createStatement();
            return dbStatement.executeQuery(query);
        } 
        catch (SQLException ex) 
        {
            Iris.log.severe(ex.getMessage());
            return null;
        }
    }
}
