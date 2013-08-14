/*
 * Establish a simple database connection
 */
package net.certifi.audittablegen;

import java.sql.*;
import java.util.Properties;
import javax.naming.*;
import javax.sql.*;
import org.apache.commons.cli.CommandLine;
//import java.util.*;
import org.postgresql.jdbc4.*;
import org.postgresql.Driver;

/**
 *
 * @author Glenn Sacks
 */
final class GetConnection {
 
    static Connection connectionFromOptions(Properties prop) {

        String connectionURL;
        
        if (prop.containsKey("url")){
            connectionURL = prop.getProperty("url");
        }
        else {
            connectionURL = "jdbc:" + prop.getProperty("driver") + "://" + prop.getProperty("Server");
            connectionURL.concat(":" + prop.getProperty("port","5432")); 
            connectionURL.concat("/" + prop.getProperty("Database"));
        }
        // = "jDbc:postgresql://localhost:5432/" + prop.getProperty("Database");
        System.out.println("url = " + connectionURL);
        Connection conn = null;
        //prop.setProperty("ssl", "true");

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connectionURL, prop);
            //conn = DriverManager.getConnection(connectionURL, "testuser","testuserpw");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(2);
        }
        return conn;
    }
    

}
