package net.certifi.audittablegen;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import org.apache.commons.cli.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.postgresql.jdbc4.*;
import org.postgresql.Driver;


/**
 * Audit Table Gen Interrogate the target database and auto-generate audit
 * tables and triggers
 *
 */
public class AuditTableGen {

    Properties connectionProperties;
    DatabaseMetaData dmd;
    DataSourceDMR dmr;
    String driver;
    String catalog;
    String schema;

    /**
     * Constructor, takes a dataSource and sets up some basic instance
     * variables.
     *
     * @param dataSource
     * @throws SQLException
     */
    AuditTableGen(DataSource dataSource) throws SQLException {

        //do stuff
        Connection connection = dataSource.getConnection();
        //connectionProperties = connection.getClientInfo();
        dmd = connection.getMetaData();
        dmd.getDriverMajorVersion();
        dmd.getDriverMinorVersion();
        dmd.getDriverName();
        dmd.getDriverVersion();

        try {
            catalog = connection.getCatalog();
            //schema = connection.getSchema();
        } catch (SQLException e) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, e);
        }

        if (false) {
            //known dataSource with specific implementation requirements
            //ie PostgrresDMR, HsqldbDMR...            
        } else {
            //generic implementation
            dmr = new GenericDMR(dataSource);
        }

    }

    String GetDataSourceInfo() throws SQLException {

        StringBuilder s = new StringBuilder();
        s.append("Driver Name: " + dmd.getDriverName() + System.lineSeparator());
        s.append("Driver Version: " + dmd.getDriverVersion() + System.lineSeparator());
        s.append("CatalogSeperator: " + dmd.getCatalogSeparator() + System.lineSeparator());
        s.append("CatalogTerm: " + dmd.getCatalogTerm() + System.lineSeparator());
        s.append("SchemaTerm: " + dmd.getSchemaTerm() + System.lineSeparator());
        s.append("Catalogs: ");
        ResultSet rs = dmd.getCatalogs();
        while (rs.next()) {
            s.append(rs.getString("TABLE_CAT") + ",");
        }
        rs.close();
        s.append(System.lineSeparator());

        s.append("Schemas: ");
        rs = dmd.getSchemas();
        while (rs.next()) {
            s.append("{catalog}:" + rs.getString("TABLE_CATALOG") + " {schema}:" + rs.getString("TABLE_SCHEM") + ",");
        }
        rs.close();
        s.append(System.lineSeparator());
        s.append("Target Catalog: " + catalog + System.lineSeparator());
        s.append("Target Schema: " + schema + System.lineSeparator());

        return s.toString();

    }

    public static void main(String[] args) {
        Properties prop;
        Options options = new Options();
        options.addOption("h", "help", false, "display this message");
//        options.addOption("d", "Database", true, "Name of the database to connect to");
//        options.addOption("s", "Server", true, "Name of the Server to connect to");
        options.addOption("driver", true, "jdbc driver name (hsqldb, postgres, or full class name on classpath)");
        options.addOption("u", "user", true, "DB server login username");
        options.addOption("p", "password", true, "DB server login password");
        options.addOption("f", "filename", true, "name of file to store the script");
        options.addOption("url", true, "full url to DB.  Overrides -d, -s, --driver");
        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        AuditTableGen atg;
        DataSource ds;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                usage(options);
                return;
            }

            prop = GetRunTimeProperties(cmd);

            if (!prop.getProperty("validArgs", "false").equals("true")) {
                usage(options);
                return;
            }

        } catch (ParseException ex) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
            return; //return here just to make the compiler shut-up
        }

        try {
            ds = GetRunTimeDataSource(prop);
            atg = new AuditTableGen(ds);
            System.out.print(atg.GetDataSourceInfo());

            //DataSourceDMR dsDMR = GetDataSourceDMR (cmd);
//            Connection conn = GetConnection.ConnectionFromOptions(prop);
//            TestConnection.GetData(conn);
//            conn.close(); 
        } catch (SQLException ex) {
            Logger.getLogger(AuditTableGen.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Done.");
    }

    static void usage(Options options) {

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("AuditTableGen", options);

    }

    /**
     * Convert the command arguments to properties must contain either a
     * decipherable jdbc url or database and server params, plus a username and
     * password.
     *
     * @param cmd
     * @return
     */
    static Properties GetRunTimeProperties(CommandLine cmd) {

        Boolean isValid = true;
        Properties prop = new Properties();

        if (cmd.hasOption("url")) {
            String url = cmd.getOptionValue("url");
            String subschema_uri = url.substring(5);

            URI uri = URI.create(url);
            if (!uri.getScheme().equalsIgnoreCase("jdbc")) {
                System.out.println("Invalid url: '" + url + "'");
                isValid = false;
            } else {
                uri = URI.create(subschema_uri);
                prop.setProperty("driver", uri.getScheme());
                prop.setProperty("url", url);
            }
        } else {
            //not going to worry about parsing driver,db,server for now
            //just require a url, or connect the in mem database
//            List<String> argList = Arrays.asList("driver","Database","Server");           
//            for ( String arg : argList ){
//                if (cmd.hasOption(arg)){
//                    prop.setProperty(arg, cmd.getOptionValue(arg));
//                }
//                else {
//                    System.out.println("Missing parameter: " + arg);
//                    isValid = false;
//                }
//            }            
        }

        //more required params (for now)
        if (prop.containsKey("url")) {
            List<String> argList = Arrays.asList("user", "password");
            for (String arg : argList) {
                if (cmd.hasOption(arg)) {
                    prop.setProperty(arg, cmd.getOptionValue(arg));
                } else {
                    System.out.println("Missing parameter: " + arg);
                    isValid = false;
                }
            }
        }
        //optional params
        if (cmd.hasOption("filename")) {
            prop.setProperty("filename", cmd.getOptionValue("filename"));
        }

        prop.setProperty("validArgs", isValid ? "true" : "false");

        return prop;

    }

    static DataSource GetRunTimeDataSource(Properties props) {

        DataSource ds;
        String driver;
        String validation = "select 1"; //postgres is the defualt

        if (props.containsKey("url")) {
            driver = props.getProperty("driver", "");
            if (driver.equals("hsqldb")) {
                props.setProperty("driver", "org.hsqldb.jdbcDriver");
            } else if (driver.equals("postgresql")) {
                props.setProperty("driver", "org.postgresql.Driver");
            } else {
                //nothing.  Hope driver is a valid driver class.
            }
        } else {
            //in memory hsqldb
            props.setProperty("user", "sa");
            props.setProperty("password", "");
            props.setProperty("url", "jdbc:hsqldb:mem:aname");
            props.setProperty("driver", "org.hsqldb.jdbcDriver");
        }
        
        driver = props.getProperty("driver");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(props.getProperty("driver"));
        dataSource.setUsername(props.getProperty("user"));
        dataSource.setPassword(props.getProperty("password"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        if (driver.contains("hsqldb")){
            validation = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
        }
        dataSource.setValidationQuery(validation);
        ds = dataSource;

        return ds;
    }
}
