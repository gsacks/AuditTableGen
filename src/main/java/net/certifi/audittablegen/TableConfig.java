/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Glenn Sacks
 */
public class TableConfig {
    
    Boolean hasInsertTrigger = true;
    Boolean hasUpdateTrigger = true;
    Boolean hasDeleteTrigger = true;
    String tableName;
    Map<String, Map<String,String>> columns; //column meta data
    
    /** excluded columns are represented in the audit table, but changes
     *  to the data in excluded columns to not cause the insert or
     *  update triggers to fire.
     */
    HashSet<String> excludedColumns;
    HashSet<String> includedColumns;

    TableConfig(String tableName){
       excludedColumns =  new HashSet<String>();
       this.tableName = tableName;
    }

    public Map<String, Map<String, String>> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Map<String, String>> columns) {
        this.columns = columns;
    }
    
    void addExcludedColumn(String columnName){
        excludedColumns.add(columnName);
    }
    
    public HashSet<String> getExcludedColumns() {
        return new HashSet(excludedColumns);
    }
        
    void addIncludedColumn(String columnName){
        includedColumns.add(columnName);
    }
    
    public HashSet<String> getIncludedColumns() {
        return new HashSet(includedColumns);
    }
    
    Boolean getHasDeleteTrigger() {
        return hasDeleteTrigger;
    }

    void setHasDeleteTrigger(Boolean hasDeleteTrigger) {
        this.hasDeleteTrigger = hasDeleteTrigger;
    }

    Boolean getHasInsertTrigger() {
        return hasInsertTrigger;
    }

    void setHasInsertTrigger(Boolean hasInsertTrigger) {
        this.hasInsertTrigger = hasInsertTrigger;
    }

    Boolean getHasUpdateTrigger() {
        return hasUpdateTrigger;
    }

     void setHasUpdateTrigger(Boolean hasUpdateTrigger) {
        this.hasUpdateTrigger = hasUpdateTrigger;
    }
    
    
    
    
    
    
}
