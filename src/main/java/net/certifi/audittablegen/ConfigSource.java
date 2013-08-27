/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigSource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigSource.class);
    
    Map<String, TableConfig> tablesConfig;
    HashSet<String> existingAuditTables;
    String tablePrefix = "zz_";
    String tablePostfix = "";
    String columnPrefix = "";
    String columnPostfix = "";
    
    ConfigSource(){
        tablesConfig = new HashMap<String, TableConfig>();
        existingAuditTables = new HashSet<String>();
    }
    
    void addExistingAuditTable (String auditTableName){
        existingAuditTables.add(auditTableName);
    }
    
    Boolean hasExistingAuditTable (String auditTableName){
        return existingAuditTables.contains(auditTableName);
    }
    
    void addTableConfig (String tableName){
        
        if(tablesConfig.containsKey(tableName)){
            //table already exists
            return;
        }
        else {
            TableConfig tc = new TableConfig(tableName);
            tablesConfig.put(tableName, tc);
            return;
        }
    }
    
    void ensureTableConfig (String tableName){
        addTableConfig(tableName);
    }
    
    void addExcludedColumn (String tableName, String columnName){
    
        TableConfig tc;
        
        if(tablesConfig.containsKey(tableName)){
            tc = tablesConfig.get(tableName);
        }
        else {
            tc = new TableConfig(tableName);
            tablesConfig.put(tableName, tc);
        }
        
        tc.addExcludedColumn(columnName);
        
    }
    
    void addIncludedColumn (String tableName, String columnName){
    
        TableConfig tc;
        
        if(tablesConfig.containsKey(tableName)){
            tc = tablesConfig.get(tableName);
        }
        else {
            tc = new TableConfig(tableName);
            tablesConfig.put(tableName, tc);
        }
        
        tc.addIncludedColumn(columnName);
        
    }

    TableConfig getTableConfig (String tableName){
        return tablesConfig.get(tableName);
    }
    
    String getTablePostfix() {
        return tablePostfix;
    }

    void setTablePostfix(String tablePostfix) {
        this.tablePostfix = tablePostfix;
    }

    String getTablePrefix() {
        return tablePrefix;
    }

    void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    String getColumnPostfix() {
        return columnPostfix;
    }

    void setColumnPostfix(String columnPostfix) {
        this.columnPostfix = columnPostfix;
    }

    String getColumnPrefix() {
        return columnPrefix;
    }

    void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = columnPrefix;
    }
    
    
}
