/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import com.google.common.base.Throwables;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
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
    ConfigSource configSource;
    
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
        if (schema == null){
            targetSchema = null;
        }
        else {
            targetSchema = schema;
        }
        
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
        DatabaseMetaData dmd;
        
        try {
            ResultSet rs;
            dmd = getConnection().getMetaData();
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
            dmd.getConnection().close();
            
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

    Connection getConnection(){
        
        try {
            Connection conn = dataSource.getConnection();
            if (!targetSchema.isEmpty()){
                conn.setSchema(targetSchema);
            }
            return conn;
        } catch (SQLException ex) {
            logger.error("Error getting connection:", ex);
            //return null;
            throw Throwables.propagate(ex);
        }
        
    }
    
    //This does keep an open connection.  Calling method should close it.
    DatabaseMetaData getMetaData(){
        
        Connection conn = getConnection();
        try {
            return conn.getMetaData();
        } catch (SQLException ex) {
            logger.error("Error getting metaData:", ex.getMessage());
            return null;
        }
        
    }
    
    void loadConfigAttributes(ConfigSource configSource){
        
        StringBuilder builder = new StringBuilder();
        builder.append("select attribute, table, column, value from ").append(auditConfigTable);
                
        try {
            
            Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(builder.toString());
            String table;
            String column;
            String value;
            
            while (rs.next()){
                //load attributes into configSource
                
                //initially not worrying about wildcards.
                //TODO handle regexp match in ConfigSource.
                if (rs.getString("attribute").equals("exclude")){
                    //parse exclude attribute
                    table = rs.getString("table").trim();
                    column = rs.getString("column").trim();
                    configSource.addExcludedColumn(table, column);                    
                }
                
                //all columns are included by default, unless specifically
                //excluded.  This is here to handle overriding a
                //regexp match in the exclude list.
                //TODO implement regexp match in ConfigSource.
                else if (rs.getString("attribute").equals("include")){
                    //parse include attribute
                    table = rs.getString("table").trim();
                    column = rs.getString("column").trim();
                    configSource.addIncludedColumn(table, column);         
                }
                else if (rs.getString("attribute").equals("tablepreifx")){
                    //parse table prefix attribute
                    configSource.setTablePrefix(rs.getString("value").trim());
                }
                else if (rs.getString("attribute").equals("tablepostfix")){
                    //parse table postfix attribute
                    configSource.setTablePostfix(rs.getString("value").trim());
                }
                else if (rs.getString("attribute").equals("columnpreifx")){
                    //parse column prefix attribute
                    configSource.setColumnPrefix(rs.getString("value").trim());
                }
                else if (rs.getString("attribute").equals("columnpostfix")){
                    //parse column postfix attribute
                    configSource.setColumnPostfix(rs.getString("value").trim());
                }
                
                //these are unlikely to be used.  Value of 'false' turns the
                //trigger off.  Any other value is true.  If not set here, the
                //default is true for all triggers.
                else if (rs.getString("attribute").equals("auditinsert")){
                    //parse audit insert attribute
                    table = rs.getString("table").trim();
                    configSource.ensureTableConfig(table);
                    value = rs.getString("value").toUpperCase().trim();
                    configSource.getTableConfig(table)
                            .setHasInsertTrigger(value.equals("FALSE") ? false : true);
                }
                 else if (rs.getString("attribute").equals("auditupdate")){
                    //parse audit update attribute
                    table = rs.getString("table").trim();
                    configSource.ensureTableConfig(table);
                    value = rs.getString("value").toUpperCase().trim();
                    configSource.getTableConfig(table)
                            .setHasUpdateTrigger(value.equals("FALSE") ? false : true);
                }
                 else if (rs.getString("attribute").equals("auditdelete")){
                    //parse audit delete attribute
                    table = rs.getString("table").trim();
                    configSource.ensureTableConfig(table);
                    value = rs.getString("value").toUpperCase().trim();
                    configSource.getTableConfig(table)
                            .setHasDeleteTrigger(value.equals("FALSE") ? false : true); 
                }
            }         
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException ex) {
            logger.error("Error retrieving audit configuration" + ex.getMessage());
        }
             
    }
    
    void loadConfigTables (ConfigSource configSource){
     
        String table;
        
        try {
            
            DatabaseMetaData dmd = getMetaData();
            ResultSet rs = dmd.getTables(null, targetSchema, null, new String[]{"TABLE"});
            
            while (rs.next()){
                table = rs.getString("TABLE_NAME").trim();
                configSource.ensureTableConfig(table);
            }
            
            dmd.getConnection().close();
            
        } catch (SQLException e){
            logger.error("SQL error retrieving table list: " + e.getMessage());
            return;
        }
        
        for ( Map.Entry <String, TableConfig> entry : configSource.tablesConfig.entrySet()){
            entry.getValue().columns = getColumnMetaDataForTable(entry.getKey());
        }
        
        return;
 
    }
    
    void loadConfigAuditTables (ConfigSource configSource){
     
        String table;
        
        String tablePattern = configSource.tablePrefix + "%" + configSource.tablePostfix;
        try {
            
            DatabaseMetaData dmd = getMetaData();
            ResultSet rs = dmd.getTables(null, targetSchema, tablePattern, new String[]{"TABLE"});
            
            while (rs.next()){
                table = rs.getString("TABLE_NAME").trim();
                configSource.addExistingAuditTable(table);
            }
            
            dmd.getConnection().close();
            
        } catch (SQLException e){
            logger.error("SQL error retrieving table list: " + e.getMessage());
            return;
        }
        
        return;
 
    }
    
    String getAuditTableSql(ConfigSource configSource, String tableName){
        
        String sql;
        
        String auditTableName =
                configSource.tablePrefix
                + tableName
                + configSource.tablePostfix;
        
        //check if table already exists
        if (configSource.existingAuditTables.contains(auditTableName)){
            sql = getAuditTableModifySql(configSource, tableName);
        }
        else {
            sql = getAuditTableCreateSql(configSource, tableName);
        }
        
        return sql;
    }
    
    Map getColumnMetaDataForTable (String tableName){
        
        Map columns = new HashMap<String, HashMap<String, String>>();
        
        try {
            DatabaseMetaData dmd = getConnection().getMetaData();
            ResultSet rs = dmd.getColumns(null, targetSchema, tableName, null);
            
            //load all of the metadata in the result set into a map for each column
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int metaDataColumnCount = rsmd.getColumnCount();
            if (! rs.isBeforeFirst()) {
                throw new RuntimeException("No results for DatabaseMetaData.getColumns(" + targetSchema + "." + tableName + ")");
            }
            while (rs.next()){
                Map columnMetaData = new HashMap<String, String>();
                for (int i = 1; i <= metaDataColumnCount; i++){
                    columnMetaData.put(rsmd.getColumnName(i), rs.getString(i));
                }
                columns.put(rs.getString("COLUMN_NAME"), columnMetaData);                        
            }
            
        }
        catch (SQLException e) {
            throw Throwables.propagate(e);
        }
        
        return columns;
        
    }
    
    String getAuditTableCreateSql(ConfigSource configSource, String tableName){
        
        StringBuilder builder = new StringBuilder();
        
        TableConfig tc = configSource.getTableConfig(tableName);
        
        //TODO everything.
        
        
        return builder.toString();
    }
    
    String getAuditTableModifySql(ConfigSource configSource, String tableName){
        
        StringBuilder builder = new StringBuilder();
        
        TableConfig tc = configSource.getTableConfig(tableName);
        
        //TODO everything.
        
        
        return builder.toString();
    }
}
