package net.certifi.audittablegen;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import javax.sql.*;
import org.apache.commons.cli.*;

/**
 * Audit Table Gen
 * Interrogate the target database and auto-generate audit tables and triggers
 *
 */
public class AuditTableGen 
{
    public static void main( String[] args )
    {
        String db;
        String server;
        String user;
        String pw;
        
        Options options = new Options();
        options.addOption("h","help", false, "display this message");
        options.addOption("d","Database", true, "Name of the database to connect to");
        options.addOption("s","Server", true, "Name of the Server to connect to");
        options.addOption("u","Username",true,"Username");
        options.addOption("p","Password",true,"Password");
        options.addOption("f","filename", true, "name of file to store the script");
        options.addOption("url", true, "full url to DB.  Overrides -d & -s");
        CommandLineParser parser = new GnuParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                usage(options);
                return;
            }
            
            Connection conn = GetConnection.ConnectionFromOptions(cmd);
            TestConnection.GetData(conn);
            conn.close();
            
        } catch (ParseException ex) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (SQLException ex) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        //options.getOption("Database").setRequired(true);
        
        System.out.println( "Done." );
    }
    
    public static void usage (Options options){
        
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("AuditTableGen", options);
        
    }
}
