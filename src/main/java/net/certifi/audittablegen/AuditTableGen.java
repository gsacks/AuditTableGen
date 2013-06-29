package net.certifi.audittablegen;

import java.util.logging.Level;
import java.util.logging.Logger;
//import java.sql;
//import javax.sql;
import org.apache.commons.cli.*;

/**
 * Audit Table Gen
 * This is just a shell app generated by NetBeans as a Maven Java project
 *
 */
public class AuditTableGen 
{
    public static void main( String[] args )
    {
        
        Options options = new Options();
       
        options.addOption("help", false, "display this message");
        options.addOption("Database", true, "Name of the database to connect to");
        options.addOption("Server", true, "Name of the Server to connect to");
        options.addOption("Username",true,"Username");
        options.addOption("Password",true,"Password");
        options.addOption("filename", true, "name of file to store the script");
        
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                usage(options);
                return;
            }
        } catch (ParseException ex) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        System.out.println( "This does not do anything yet." );
    }
    
    public static void usage (Options options){
        
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("AuditTableGen", options);
        
    }
}