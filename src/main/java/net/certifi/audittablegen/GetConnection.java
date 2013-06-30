/*
 * Establish a simple database connection
 */
package net.certifi.audittablegen;

import java.sql.*;
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
 
    static Connection ConnectionFromOptions(CommandLine cmd) {

        String connectionURL = "jdbc:postgresql://localhost:5432/" + cmd.getOptionValue("Database");
        String url = "jdbc:postgresql://localhost:5432/testSandbox?user=postgres&password=secret&ssl=true";
        Connection conn = null;        

        try {
            Class.forName("org.postgresql.Driver");
            //conn = DriverManager.getConnection(connectionURL, cmd.getOptionValue("user"), cmd.getOptionValue("password"));
            conn = DriverManager.getConnection(connectionURL, "testuser","testuserpw");
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
