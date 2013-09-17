package net.certifi.audittablegen;

import com.google.common.base.Throwables;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import javax.sql.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.commons.dbcp.BasicDataSource;

/**
 * Audit Table Gen Interrogate the target database and auto-generate audit tables and triggers
 *
 */
public class AuditTableGen {

    private static final Logger logger = LoggerFactory.getLogger(AuditTableGen.class);
    DataSource dataSource;
    DataSourceDMR dmr;
    String driver;
    String catalog;
    String schema;
    Boolean initialized = false;

    /**
     * Constructor, takes a dataSource and sets up some basic instance variables.
     *
     * @param dataSource
     * @throws SQLException
     */
    AuditTableGen(DataSource dataSource, String targetSchema){
        
        this.dataSource = dataSource;
        this.schema = targetSchema;
        
    }
    
    /**
     * Validates the provided dataSource and gets a DataSourceDMR
     * object to manage database interaction.  Sets initialized flag
     * to true if initialization is successful.
     * @throws SQLException 
     */
    void initialize()throws SQLException {

        Connection connection = dataSource.getConnection();
        //Properties connectionProperties = connection.getClientInfo();
        DatabaseMetaData dmd = connection.getMetaData();
        
        logger.debug("DatabaseProduct: {}", dmd.getDatabaseProductName());

        try {
            catalog = connection.getCatalog();

            if (schema.isEmpty() || schema == null) {
                try {
                    schema = connection.getSchema();
                } catch (AbstractMethodError e) {
                    logger.error("Abstract method getSchema() not implemented", e);
                    schema = "";
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting catalog/schema", e);
            
        }

        if (dmd.getDriverName().toLowerCase().contains("postgresql")) {
            dmr = new PostgresqlDMR(dataSource, schema);
            //known dataSource with specific implementation requirements
            //ie PostgrresDMR, HsqldbDMR...            
        }
        if (dmd.getDriverName().toLowerCase().contains("hsqldb")) {
            dmr = new HsqldbDMR(dataSource, schema);
            //known dataSource with specific implementation requirements
            //ie PostgrresDMR, HsqldbDMR...            
        } else {
            //generic implementation
            dmr = new GenericDMR(dataSource, schema);
            logger.info("attempting to run against unknown database product");
        }
        
        if (dmr != null){
            this.initialized = true;
        }
        
        if (schema != null && !schema.isEmpty()) {
            dmr.setSchema(schema);
            
            if (dmr.getSchema() == null){
                throw new RuntimeException ("Schema could not be found.");
            }
        }

    }
    
    /**
     * Executes audit table update to the database.  If audit configuration
     * tables are not present, this will generate the configuration tables.
     * If the configuration already exists, then it will generate the audit
     * tables themselves.
     * 
     * @return true if update is successful at either generating new
     * audit configuration tables or the actual audit tables.
     */
    Boolean updateAuditTables() {
        
        String message;
        
        if (!this.initialized){
            try {
                initialize();
            } catch (SQLException ex) {
                logger.error("Cannot initialize connection to the dataSource", ex);
                return false;
            }
        }
        
        if (!dmr.hasAuditConfigTable()){
            message = "Audit configuration tables missing. Generating...";
            System.out.println(message);
            logger.info(message);
            
            dmr.createAuditConfigTable();
            
            if (!dmr.hasAuditConfigTable()){
                message = "Failed to generate audit configuration tables.";
                System.out.println(message);
                logger.error(message);
                return false;
            }
            else 
           {
                message = "Audit configuratiion tables created.";
                System.out.println(message);
                logger.info(message);
                return true;
            }
        }
        else {
            ConfigSource configSource = new ConfigSource();

            configSource.addAttributes(dmr.getConfigAttributes());
            configSource.addTables(dmr.getTables());
            configSource.setMaxUserNameLength(dmr.getMaxUserNameLength());
            
            ChangeSourceFactory factory = new ChangeSourceFactory(configSource);
            
            List<DBChangeUnit> unitList = factory.getDBChangeList();
            if (DBChangeUnit.validateUnitList(unitList)) {
                dmr.readDBChangeList(unitList);
                dmr.executeChanges();
            } else {
                logger.error("Program error. Database change list not formed properly.");
                return false;
            }
            
        }
        
        return true;
        
    }
    
    /**
     * Examines the DataSource metadata for information pertaining to the
     * driver, catalog, schema and the presence of audit table configuration
     * data.
     * 
     * @return String containing datasource information.
     * @throws SQLException 
     */
    String getDataSourceInfo() throws SQLException {

        Connection conn = dataSource.getConnection();
        DatabaseMetaData dmd = conn.getMetaData();
        StringBuilder s = new StringBuilder();

        s.append("Driver Name: ").append(dmd.getDriverName())
                .append("Driver Version: ").append(dmd.getDriverVersion()).append(System.lineSeparator())
                .append("CatalogSeperator: ").append(dmd.getCatalogSeparator()).append(System.lineSeparator())
                .append("CatalogTerm: ").append(dmd.getCatalogTerm()).append(System.lineSeparator())
                .append("SchemaTerm: ").append(dmd.getSchemaTerm()).append(System.lineSeparator())
                .append("Catalogs: ");

        ResultSet rs = dmd.getCatalogs();
        while (rs.next()) {
            s.append(rs.getString("TABLE_CAT")).append(",");
            logger.debug("Catalog: {}", rs.getString("TABLE_CAT"));
        }
        rs.close();
        s.append(System.lineSeparator());

        s.append("Schemas: ");
        rs = dmd.getSchemas();
        while (rs.next()) {
            logger.debug("Schema: {}", rs.getString("TABLE_SCHEM"));
            s.append("{catalog}:").append(rs.getString("TABLE_CATALOG")).append(" {schema}:").append(rs.getString("TABLE_SCHEM")).append(",");
        }
        rs.close();
        s.append(System.lineSeparator())
                .append("Target Catalog: ").append(catalog).append(System.lineSeparator())
                .append("Target Schema: ").append(schema).append(System.lineSeparator());

       if (dmr.hasAuditConfigTable()){
           s.append("Has auditConfigSource table").append(System.lineSeparator());
       }
       
       conn.close();
       
       return s.toString();

    }

    public static void main(String[] args) {
        Properties prop;
        Options options = new Options();
        options.addOption("h", "help", false, "display this message");
//        options.addOption("d", "Database", true, "Name of the database to connect to");
//        options.addOption("s", "Server", true, "Name of the Server to connect to");
        options.addOption("driver", true, "specifiy jdbc driver. Only used if can't resolve from url");
        options.addOption("u", "username", true, "DB server login username");
        options.addOption("p", "password", true, "DB server login password");
        options.addOption("f", "filename", true, "name of file to store the script");
        options.addOption("schema", true, "name of the target schema");
        options.addOption("url", true, "full url to DB.  Overrides -d, -s");
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

            prop = getRunTimeProperties(cmd);

            if (!prop.getProperty("validArgs", "false").equals("true")) {
                usage(options);
                return;
            }

        } catch (ParseException ex) {
            logger.error("Error", ex);
            throw Throwables.propagate(ex);
//            System.exit(1);
//            return; //return here just to make the compiler shut-up
        }

        try {
            ds = getRunTimeDataSource(prop);
            atg = new AuditTableGen(ds, prop.getProperty("schema", null));
            logger.info(atg.getDataSourceInfo());

            //DataSourceDMR dsDMR = GetDataSourceDMR (cmd);
//            Connection conn = GetConnection.ConnectionFromOptions(prop);
//            TestConnection.GetData(conn);
//            conn.close(); 
        } catch (SQLException ex) {
           logger.error("Error", ex);
           throw Throwables.propagate(ex);
        }

        Boolean result = atg.updateAuditTables();

        if (result){
            logger.info("success");
        }
        else {
            logger.info("failure");
        }
        
        logger.info("Done.");
    }

    static void usage(Options options) {

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("AuditTableGen", options);

    }

    /**
     * Convert the command arguments to properties must contain either a decipherable jdbc url or database and server
     * params, plus a username and password.
     *
     * @param cmd
     * @return
     */
    static Properties getRunTimeProperties(CommandLine cmd) {

        Boolean isValid = true;
        Properties prop = new Properties();
        String driver = "";
        String subSchema = ""; //should indicate the JDBC driver

        //set url property
        if (cmd.hasOption("url")) {
            String url = cmd.getOptionValue("url");
            String subschema_uri = url.substring(5); //strip jdbc:

            //rudimentary url validation
            URI uri = URI.create(url);
            if (!uri.getScheme().equalsIgnoreCase("jdbc")) {
                logger.warn("Invalid url: '{}'", url);
                isValid = false;
            } else {
                uri = URI.create(subschema_uri);
                subSchema = uri.getScheme(); //driver reference hopefully
            }

            prop.setProperty("url", url);
        }

        //set driver property
        String cmdArgDriver = cmd.getOptionValue("driver", "");
        if (subSchema.equalsIgnoreCase("postgresql")) {
            prop.setProperty("driver", "org.postgresql.Driver");
        } else if (subSchema.equalsIgnoreCase("hsqldb")) {
            prop.setProperty("driver", "org.hsqldb.jdbcDriver");
        } else if (cmdArgDriver.isEmpty()) {
            //best guess - this will almost certainly fail...
            prop.setProperty("driver", subSchema);
        } else {
            //unrecognized driver passed on command arg
            //will use it if it resolves on the class-path
            prop.setProperty("driver", cmd.getOptionValue("driver", ""));
        }

        //not going to worry about parsing db,server for now
        //just require a url, or connect to the in mem database
//      List<String> argList = Arrays.asList("driver","Database","Server");           
//      for ( String arg : argList ){
//           if (cmd.hasOption(arg)){
//                prop.setProperty(arg, cmd.getOptionValue(arg));
//            }
//            else {
//                logger.warn("Missing parameter: {}", arg);
//                isValid = false;
//            }
//        }            
//      }

        //more params (for now)
        //do not require - these can also be passed on the url
        if (prop.containsKey("url")) {
            List<String> argList = Arrays.asList("username", "password","schema");
            for (String arg : argList) {
                if (cmd.hasOption(arg)) {
                    prop.setProperty(arg, cmd.getOptionValue(arg));
//                } else {
//                    logger.warn("Missing parameter: {}", arg);
//                    isValid = false;
                }
            }
        }

        //optional params - this is for the output script
        if (cmd.hasOption("filename")) {
            prop.setProperty("filename", cmd.getOptionValue("filename"));
        }

        prop.setProperty("validArgs", isValid ? "true" : "false");

        return prop;

    }

    static DataSource getRunTimeDataSource(Properties props) {

        DataSource ds;
        String driver;

        if (props.containsKey("url")) {
            driver = props.getProperty("driver", "");
            if (driver.toLowerCase().contains("hsqldb")) {
                ds = HsqldbDMR.getRunTimeDataSource(props);
            } else if (driver.toLowerCase().contains("postgresql")) {
                ds = PostgresqlDMR.getRunTimeDataSource(props);
            } else {
                //take a shot at it with user supplied driver & url
                ds = GenericDMR.getRunTimeDataSource(props);
            }
        } else {
            //no url provided
            //in memory hsqldb - testing only
            ds = HsqldbDMR.getRunTimeDataSource();
        }

        return ds;
    }
}
