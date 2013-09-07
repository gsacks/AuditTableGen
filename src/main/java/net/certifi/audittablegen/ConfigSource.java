/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigSource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigSource.class);
    
    Map<String, TableConfig> tablesConfig;
    Map<String, TableConfig> existingAuditTables;
    String tablePrefix = "zz_";
    String tablePostfix = "";
    String columnPrefix = "";
    String columnPostfix = "";
    IdentifierMetaData idMetaData;
    
    ConfigSource(IdentifierMetaData idMetaData){
        this.idMetaData = idMetaData;
        tablesConfig = new CaseInsensitiveMap();
        existingAuditTables = new CaseInsensitiveMap();
    }
    
    void addExistingAuditTable (String auditTableName){
        
        String myAuditTableName = idMetaData.convertId(auditTableName);
        if (existingAuditTables.containsKey(myAuditTableName)){
            //table already exists
            return;
        }
        else {
            TableConfig atc = new TableConfig(myAuditTableName, idMetaData);
            existingAuditTables.put (myAuditTableName, atc);
            return;
        }

    }
    
    Boolean hasExistingAuditTable (String auditTableName){
        
        return existingAuditTables.containsKey(auditTableName);
    }
    
    /**
     * Add table to the map of database tables.  Note that the table
     * name is stored in the TableConfig object case sensitive, but the
     * map key is case insensitive.  If the table already exists, it will
     * not be replaced.
     * 
     * @param tableName 
     */
    void addTableConfig (String tableName){
        
        if(tablesConfig.containsKey(tableName)){
            //table already exists
            return;
        }
        else {
            TableConfig tc = new TableConfig(tableName, idMetaData);
            tablesConfig.put(tableName, tc);
            return;
        }
    }
    
    /**
     * A proxy for addTableConfig
     * 
     * @param tableName 
     */
    void ensureTableConfig (String tableName){
        addTableConfig(tableName);
    }
    
    /**
     * Finds the table in the existing table map and adds the column
     * to the list of columns to be excluded from triggering an update
     * to the audit table.  Note that the case sensitive table and column
     * name are stored, but the key lookup is case insensitive.
     * 
     * @param tableName The name of the table containing the column
     * @param columnName The column name.
     */
    void addExcludedColumn (String tableName, String columnName){
    
        TableConfig tc;
        
        String myTableName = idMetaData.convertId(tableName);
        String myColumnName = idMetaData.convertId(columnName);
        
        if(tablesConfig.containsKey(myTableName)){
            tc = tablesConfig.get(myTableName);
        }
        else {
            tc = new TableConfig(myTableName, idMetaData);
            tablesConfig.put(myTableName, tc);
        }
        
        tc.addExcludedColumn(myColumnName);
        
    }
    
    /**
     * Finds the table in the existing table map and adds the column
     * to the list of columns to be included to trigger an update
     * to the audit table.  Note that the case sensitive table and column
     * name are stored, but the key lookup is case insensitive.  The default
     * is for all columns to trigger an update.  The include list is intended
     * to act as an override for regexp matches in the set-up which may be
     * overly broad.
     * 
     * @param tableName The name of the table containing the column
     * @param columnName The column name.
     */
    void addIncludedColumn (String tableName, String columnName){
    
        TableConfig tc;
        
        if(tablesConfig.containsKey(tableName)){
            tc = tablesConfig.get(tableName);
        }
        else {
            tc = new TableConfig(tableName, idMetaData);
            tablesConfig.put(tableName, tc);
        }
        
        tc.addIncludedColumn(columnName);
        
    }

    TableConfig getTableConfig (String tableName){
        
        return tablesConfig.get(tableName);
    }
    
    String getTablePostfix() {
        return idMetaData.convertId(tablePostfix);
    }

    void setTablePostfix(String tablePostfix) {
        this.tablePostfix = tablePostfix;
    }

    String getTablePrefix() {
        return idMetaData.convertId(tablePrefix);
    }

    void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    String getColumnPostfix() {
        return idMetaData.convertId(columnPostfix);
    }

    //not supported - can wreak havok with table ids
    void setColumnPostfix(String columnPostfix) {
        this.columnPostfix = "";
        //this.columnPostfix = columnPostfix;
    }

    String getColumnPrefix() {
        return idMetaData.convertId(columnPrefix);
    }

    //not supported - can wreak havoc with table ids
    void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = "";
        //this.columnPrefix = columnPrefix;
    }
    
}
