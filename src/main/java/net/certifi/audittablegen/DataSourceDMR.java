/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.List;


/**
 *
 * @author Glenn Sacks
 */
public interface DataSourceDMR {
    
    /**
     * Check for existence of the audit configuration tables.  The lookup is
     * not case sensitive.
     * @return True if the audit tables are present in the
     * DataSource (and schema, if a schema is set).
     */
    Boolean hasAuditConfigTable();
           
    /**
     * generate new audit configuration tables in the targeted database/schema
     */
    void createAuditConfigTable();

    /**
     * Set the schema. The input value is recorded to the an unvalidated schema
     * variable.  It is then looked up in the database, and if there is
     * a case insensitive match, then the matched value is recorded to a
     * validated schema variable, or null if there is no match.
     * @param schema 
     */
     void setSchema(String schema);
    
    /**
     * Get the schema
     * @return Returns the validated schema name. If the schema
     * cannot be validated against the DataSource, then return value is null.
     */
    String getSchema();
    
    /**
     * Set an alternate name for the audit configuration table.  The default
     * is AuditConfig.  The value is stored case sensitive, but the lookup
     * against the database is not case sensitive.
     * @param table 
     */
    void setAuditConfigTableName(String table);
    
    /**
     * Get the audit configuration table name.
     * @return If the table name cannot be validated against
     * the DataSource, then return value is null. 
     */
    String getAuditConfigTableName();
            
    /**
     * Read the configuration attributes from the audit configuration
     * table in the target database/schema and return as a list
     *
     * @return List of ConfigAttribute objects or an empty list if none are found.
     */
    List getConfigAttributes();
    
    /**
     * Get List of TableDef objects for all tables
     * in the targeted database/schema
     * 
     * @return List of TableDef objects or an empty list if none are found.
     */
    List getTables ();
    
    /**
     * Get List of ColumnDef objects for all tables
     * in the targeted database/schema
     * 
     * @param tableName
     * @return List of ColumnDef objects or an empty list if none are found.
     */
    List getColumns (String tableName);
    
    /**
     * Get the max user name length from MetaData
     * @return 
     */
    int getMaxUserNameLength();
    
    /**
     * Read an ordered list of db change commands into a buffer
     * 
     * @param units 
     */
    void readDBChangeList(List<DBChangeUnit> units);
    
    /**
     * Execute the db change commands currently in the change buffer
     */
    void executeChanges();
    
    /**
     * Read an ordered list of db change commands into a buffer and execute
     * them.  Any commands currently in the buffer will be executed ahead
     * of the units in the supplied list.
     * 
     * @param units 
     */
    void executeDBChangeList(List<DBChangeUnit> units);
    
    /**
     * Discard any db change commands currently in the change buffer
     * without executing them.
     */
    void purgeDBChanges();
    
//    void createTable (String tableName);
//    void addColumn (String tableName, ColumnDef columnDef);
//    void renameColumn (String tableName, String columnName);
//    void alterColumn (String tableName, ColumnDef columnDef);
    

}

    

