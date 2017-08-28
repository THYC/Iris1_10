package net.teraoctet.iris.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionSQLite
{
    private String DBPath = "Chemin aux base de donnée SQLite";
    private Connection connection = null;
    private Statement statement = null;
 
    public ConnexionSQLite(String dBPath) 
    {
        DBPath = dBPath;
    }
 
    public void connect() 
    {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
            statement = connection.createStatement();
            System.out.println("Connexion a " + DBPath + " avec succès");
        } catch (ClassNotFoundException | SQLException notFoundException) {
            System.out.println("Erreur de connecxion Sqlite");
        }
    }
 
    public void close() 
    {
        try 
        {
            connection.close();
            statement.close();
        } 
        catch (SQLException e) 
        {
        }
    }
}

