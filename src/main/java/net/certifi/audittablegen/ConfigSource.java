/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.HashMap;
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
    Map<String, TableConfig> existingAuditTables;
    String tablePrefix = "zz_";
    String tablePostfix = "";
    String columnPrefix = "";
    String columnPostfix = "";
    IdentifierMetaData idMetaData;
    
    ConfigSource(IdentifierMetaData idMetaData){
        this.idMetaData = idMetaData;
        tablesConfig = new HashMap<>();
        existingAuditTables = new HashMap<>();
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
        
        String myAuditTableName = idMetaData.convertId(auditTableName);
        return existingAuditTables.containsKey(myAuditTableName);
    }
    
    void addTableConfig (String tableName){
        
        String myTableName = idMetaData.convertId(tableName);
        if(tablesConfig.containsKey(myTableName)){
            //table already exists
            return;
        }
        else {
            TableConfig tc = new TableConfig(myTableName, idMetaData);
            tablesConfig.put(myTableName, tc);
            return;
        }
    }
    
    void ensureTableConfig (String tableName){
        addTableConfig(tableName);
    }
    
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
    
    void addIncludedColumn (String tableName, String columnName){
    
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
        
        tc.addIncludedColumn(myColumnName);
        
    }

    TableConfig getTableConfig (String tableName){
        
        String myTableName = idMetaData.convertId(tableName);
        
        return tablesConfig.get(myTableName);
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
