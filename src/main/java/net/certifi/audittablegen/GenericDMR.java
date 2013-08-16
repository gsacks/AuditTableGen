/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Glenn Sacks
 */
class GenericDMR implements DataSourceDMR {
    private static final Logger logger = LoggerFactory.getLogger(GenericDMR.class);
    DataSource dataSource;
    String databaseProduct;
    String targetSchema;
    String auditConfigTable;
    
    /**
     * 
     * @param ds A DataSource. Unless set elsewhere,
     * the default database/schema will be targeted.
     * 
     * @throws SQLException 
     */
    GenericDMR (DataSource ds) throws SQLException{
        
        this (ds, null);
        
    }
    /**
     *
     * @param ds A DataSource
     *
     * @param schema Name of schema to perform operations upon.
     * @throws SQLException
     */
    GenericDMR(DataSource ds, String schema) throws SQLException {

        dataSource = ds;
        Connection conn = ds.getConnection();
        DatabaseMetaData dmd;
        dmd = conn.getMetaData();
        targetSchema = schema;
        databaseProduct = dmd.getDatabaseProductName();
        auditConfigTable = "auditconfig";
        conn.close();

    }

    /**
     * Set the schema to perform operations upon.
     * 
     * @param schema 
     */
    void setSchemaName (String schema){
        targetSchema = schema;
    }
    
    /**
     * Generate a DataSource from Properties 
     * @param props
     * @return BasicDataSource as DataSource
     */
    static DataSource getRunTimeDataSource(Properties props){
        
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName(props.getProperty("driver", ""));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        
        //dataSource.setValidationQuery("SELECT 1");
        
        return dataSource;
    }
    
    /**
     * Return true of the audit configuration source is
     * avaliable.  Only one source is currently supported, and
     * that is a table in the target database/schema named
     * auditconfig.
     * 
     * @return 
     */
    public Boolean hasConfigSource (){
        
        Boolean retval = false;
        
        try {
            ResultSet rs;
            DatabaseMetaData dmd = dataSource.getConnection().getMetaData();
            if (dmd.storesLowerCaseIdentifiers()){
                rs = dmd.getTables(null, null == targetSchema ? null : targetSchema.toLowerCase(), auditConfigTable.toLowerCase(), null);
                logger.debug("running lower case");
            }
            else if (dmd.storesMixedCaseIdentifiers()){
                rs = dmd.getTables(null, null == targetSchema ? null : targetSchema, auditConfigTable, null);
                logger.debug("running mixed case");
            }
            else {
                rs = dmd.getTables(null, null == targetSchema ? null : targetSchema.toUpperCase(), auditConfigTable.toUpperCase(), null);
                logger.debug("running upper case");
            }
            
            while (rs.next()){
                if (rs.getString("TABLE_NAME").equalsIgnoreCase(auditConfigTable)){
                    //do something
                    logger.info ("Audit Configuration Found");
                    retval = true;
                }
            }
            
            if (!retval){
                logger.debug("Audit configuration source not found");
            }
        }
        catch (SQLException e){
            logger.error("SQL error retrieving audit configuration source: " + e.getMessage());
            retval = false;
        }
        
        return retval;
        
    }
}
