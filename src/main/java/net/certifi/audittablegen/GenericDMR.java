/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import com.google.common.base.Throwables;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.collections.map.CaseInsensitiveMap;
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
    String unverifiedSchema;
    String verifiedSchema;
    String unverifiedAuditConfigTable = "auditconfig";
    String verifiedAuditConfigTable;
    //IdentifierMetaData idMetaData;
   
    
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
        DatabaseMetaData dmd = conn.getMetaData();
        databaseProduct = dmd.getDatabaseProductName();
        //idMetaData = new IdentifierMetaData();

        //storing this data for potential future use.
        //not using it for anything currently
        //idMetaData.setStoresLowerCaseIds(dmd.storesLowerCaseIdentifiers());
        //idMetaData.setStoresMixedCaseIds(dmd.storesMixedCaseIdentifiers());
        //idMetaData.setStoresUpperCaseIds(dmd.storesUpperCaseIdentifiers());

        unverifiedSchema = schema;

        conn.close();

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
    @Override
    public Boolean hasAuditConfigTable (){
        
        return ( (getAuditConfigTableName() != null) ? true : false );
        
    }
   
    
    @Override
    public void createAuditConfigTable() {
        
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            String schema = getSchema();
            if (null != schema) {
                database.setDefaultSchemaName(schema);
            }
            Liquibase liquibase = new Liquibase("src/main/resources/changesets/changeset-init-config.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(null);
            database.close();
        } catch (SQLException ex) {
            logger.error("Error genereating audit configuration tables", ex);
        } catch (DatabaseException ex) {
            logger.error("Error genereating audit configuration tables", ex);
        } catch (LiquibaseException ex) {
            logger.error("Error genereating audit configuration tables", ex);
        }
  
    }
    
    public void createAuditConfigTable2() {
        
        StringBuilder builder  = new StringBuilder();
        
        builder.append("create table ").append(this.unverifiedAuditConfigTable).append("(").append(System.lineSeparator());
        builder.append("...the rest of theh create script...this will generate an error");
        
        try (Connection conn = dataSource.getConnection()) {
            String schema = getSchema();
            if (null != schema) {
                conn.setSchema(schema);
            }

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(builder.toString());
            
            stmt.close();
        } catch (SQLException ex) {
            logger.error("Error genereating audit configuration tables", ex);
        }
        
    }

    
    /**
     * Read the configuration attributes from the audit configuration
     * table in the target database/schema and return as a list
     *
     * @return A list of ConfigAttribute objects or an empty list if none are found.
     */
    @Override
    public List getConfigAttributes(){
        
        StringBuilder builder = new StringBuilder();
        builder.append("select attribute, table, column, value from ").append(verifiedAuditConfigTable);
                
        List<ConfigAttribute> attributes = new ArrayList();
        try {
            
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(builder.toString());
            
            while (rs.next()){
                //load attributes into configSource
                ConfigAttribute attrib = new ConfigAttribute();
                attrib.setAttribute(rs.getString("attribute"));
                attrib.setTableName(rs.getString("table"));
                attrib.setColumnName(rs.getString("column"));
                attrib.setValue(rs.getString("value"));
                
                attributes.add(attrib);
                
            }     
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException ex) {
            logger.error("Error retrieving audit configuration" + ex.getMessage());
        }
        
        return attributes;
             
    }
    
    /**
     * Get List of TableDef objects for all tables
     * in the targeted database/schema
     * 
     * @return ArrayList of TableDef objects or an empty list if none are found.
     */
    @Override
    public List getTables (){
     
        List<TableDef> tables = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection()){
            
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, verifiedSchema, null, new String[]{"TABLE"});
            
            while (rs.next()){
                TableDef tableDef = new TableDef();
                tableDef.setName(rs.getString("TABLE_NAME").trim());
                
//                //ToDo: handle case where table full name matches the prefix or postfi
//                if ( table.toUpperCase().startsWith(configSource.getTablePrefix().toUpperCase())
//                     && table.toUpperCase().endsWith(configSource.getTablePostfix().toUpperCase())){
//                    configSource.addExistingAuditTable(table);
//                }
//                else {
//                    configSource.ensureTableConfig(table);
//                    
//                    //just in case audit config has set up the table with the
//                    //wrong case sensitivity, update the table name with the
//                    //value returned from the db
//                    TableConfig tc = configSource.getTableConfig(table);
//                    tc.setTableName(table);
//                }
            }
            
            rs.close();
            
        } catch (SQLException e){
            logger.error("SQL error retrieving table list: ", e);
            return null;
        }
        
        for ( TableDef tableDef : tables){
            tableDef.setColumns(getColumns(tableDef.getName()));
        }
        
        return tables;
 
    }
    
    /**
     * Get List of ColumnDef objects for all tables
     * in the targeted database/schema
     * 
     * @param tableName
     * @return ArrayList of ColumnDef objects or an empty list if none are found.
     */
    @Override
    public List getColumns (String tableName){
        
        List columns = new ArrayList<>();
        
        try {
            Connection conn = dataSource.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getColumns(null, verifiedSchema, tableName, null);
            
            //load all of the metadata in the result set into a map for each column
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int metaDataColumnCount = rsmd.getColumnCount();
            if (! rs.isBeforeFirst()) {
                throw new RuntimeException("No results for DatabaseMetaData.getColumns(" + verifiedSchema + "." + tableName + ")");
            }
            while (rs.next()){
                ColumnDef columnDef = new ColumnDef();
                Map columnMetaData = new CaseInsensitiveMap();
                for (int i = 1; i <= metaDataColumnCount; i++){
                    columnMetaData.put(rsmd.getColumnName(i), rs.getString(i));
                }
                columnDef.setName(rs.getString("COLUMN_NAME"));
                columnDef.setType(rs.getString("TYPE_NAME"));
                columnDef.setSize(rs.getInt("COLUMN_SIZE"));
                columnDef.setDecimalSize(rs.getInt("DECIMAL_SIZE"));
                columnDef.setSourceMeta(columnMetaData);
            }
            
        }
        catch (SQLException e) {
            throw Throwables.propagate(e);
        }
        
        return columns;
        
    }

   @Override
    public void setSchema(String unverifiedSchema) {

        this.unverifiedSchema = unverifiedSchema;
        this.verifiedSchema = null;
        
        if(unverifiedSchema == null){
            return;
        }
        
        try (Connection conn = dataSource.getConnection()){

            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getSchemas();
            while (rs.next()) {
                if (rs.getString("TABLE_SCHEM").trim().equalsIgnoreCase(unverifiedSchema)) {
                    //store value with whatever case sensitivity it is returned as
                    verifiedSchema = rs.getString("TABLE_SCHEM").trim();
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("error verifying schema", e);
        }
    }
    
    @Override
    public String getSchema() {

        if (verifiedSchema == null
                && unverifiedSchema != null) {
            setSchema(unverifiedSchema);
        }

        return verifiedSchema;
    }
    
    @Override
    public void setAuditConfigTableName (String unverifiedTable){
        
        this.unverifiedAuditConfigTable = unverifiedTable;
        this.verifiedAuditConfigTable = null;
        String candidate = null;
        boolean multiMatch = false;
        
        if(unverifiedAuditConfigTable == null){
            return;
        }
        
        if (null == verifiedSchema){
            logger.error("attempting to verify auditConfigTable with unverified schema");
        }
        
        try (Connection conn = dataSource.getConnection()){

            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null == verifiedSchema ? null : verifiedSchema, null, null);
            while (rs.next()) {
                if (rs.getString("TABLE_NAME").trim().equalsIgnoreCase(unverifiedTable)) {
                    //store value with whatever case sensitivity it is returned as
                    if (candidate == null){
                    candidate = rs.getString("TABLE_NAME").trim();
                    }
                    else{
                        multiMatch = true;
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("error verifying auditConfigTable", e);
        }
        
        /** Fails to set verified value if more than one match.
         * This can occur if schema is not set and there are multiple
         * tables in different schemas matching the table name.
         */
        if (!multiMatch){
            this.verifiedAuditConfigTable = candidate;
        }
        
    }
    
    @Override
    public String getAuditConfigTableName(){
         if (verifiedAuditConfigTable == null
                && unverifiedAuditConfigTable != null) {
            setAuditConfigTableName(unverifiedAuditConfigTable);
        }

        return verifiedAuditConfigTable;
    }

    @Override
    public void readDBChangeList(List<DBChangeUnit> units) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeDBChangeList(List<DBChangeUnit> units) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void purgeDBChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    

//    String getAuditTableSql(ConfigSource configSource, String tableName){
//        
//        String sql;
//        
//        String auditTableName =
//                configSource.getTablePrefix()
//                + tableName
//                + configSource.getTablePostfix();
//        
//        //check if table already exists
//        Map existingAuditTables = new CaseInsensitiveMap(configSource.existingAuditTables);
//        
//        if (configSource.existingAuditTables.containsKey(auditTableName)){
//            sql = getAuditTableModifySql(configSource, tableName);
//        }
//        else {
//            sql = getAuditTableCreateSql(configSource, tableName);
//        }
//        
//        return sql;
//    }
    
    /**
     * LEGACY OF EARLY DEVELOPMENT - NOT USED
     * Copy column metaData from the source table over to the new audit
     * table.  Exclude or include columns in the audit table according
     * the the table configuration data.
     * 
     * @param configSource
     * @param tableToAudit
     * @return 
     */
//    Map getNewAuditTableColumnMetaData (ConfigSource configSource, String tableToAudit){
//        
//        TableConfig tc = configSource.getTableConfig(tableToAudit);
//        
//        Map<String, Map<String, String>> auditTableColumns = new HashMap<String, Map<String, String>>();
//        
//        for (Map.Entry<String, Map<String, String>> entry : tc.getColumns().entrySet() ){
//            
//            //ToDo: make this search look for regexp
//            //ToDo: make this case insensitive
//            String columnName = entry.getKey();
//            if (!tc.excludedColumns.containsKey(columnName)
//                || tc.includedColumns.containsKey(columnName) ){
//                //include this column in the audit table
//                String auditColumnName = configSource.getColumnPrefix()
//                        + columnName + configSource.getColumnPostfix();
//                Map auditColumnMetaData = new CaseInsensitiveMap();
//                
//                //copy metaData from primary table/column map to audit table/ciolumn map
//                for (Map.Entry<String, String> metaDataEntry : entry.getValue().entrySet() ){
//                    String metaDataKey = metaDataEntry.getKey();
//                    String metaDataValue = metaDataEntry.getValue();
//                    if (metaDataKey.equalsIgnoreCase("IS_AUTOINCREMENT")){
//                        metaDataValue="NO";
//                    }
//                    if (metaDataKey.equalsIgnoreCase("IS_GENERATEDCOLUMN")){
//                        metaDataValue="NO";
//                    }
//                    auditColumnMetaData.put(metaDataKey, metaDataValue);
//                }
//                
//                auditTableColumns.put(auditColumnName, auditColumnMetaData);
//            }
//            else {
//                logger.info("Table: %s  Column: %s excluded", tableToAudit, entry.getKey());                
//            }
//        }
//        
//        return auditTableColumns;
//        
//    }
        
//    String getAuditTableCreateSql(ConfigSource configSource, String tableName){
//        
//        StringBuilder builder = new StringBuilder();
//        
//        builder.append("create table ").append(tableName).append(System.lineSeparator());
//        
//        
//        
//        return builder.toString();
//    }
//    
//    String getAuditTableModifySql(ConfigSource configSource, String tableName){
//        
//        StringBuilder builder = new StringBuilder();
//        
//        TableConfig tc = configSource.getTableConfig(tableName);
//        
//        //TODO everything.
//        
//        
//        return builder.toString();
//    }

    //@Override
//    public String getUpdateSQL(ConfigSource configSource) {
//        
//        StringBuilder builder = new StringBuilder();
//          
//        TableConfig tc;
//        TableConfig atc;
//        Iterator iter = configSource.existingTables.entrySet().iterator();
//        while (iter.hasNext()){
//            Entry e = (Entry) iter.next();
//            tc = (TableConfig) e.getValue();
//            String key = (String) e.getKey();
//            String auditKey = 
//                    configSource.getTablePrefix()
//                    + key
//                    + configSource.getTablePostfix();
//            atc = configSource.getExistingAuditTable(auditKey); 
//            
//            //do magic SQL generation here
//            ChangeSourceFactory changeSourceFactory = new ChangeSourceFactory(configSource);
//            
//            //TO DO - get all of the changes and apply sql in a loop    
//                        
//        }
//        
//        return builder.toString();
//
//    }

}
