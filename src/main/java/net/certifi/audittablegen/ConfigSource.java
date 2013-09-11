/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;
import java.util.List;
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
    
    Map<String, TableConfig> existingTables;
    Map<String, TableConfig> existingAuditTables;
    String tablePrefix = "zz_";
    String tablePostfix = "";
    String columnPrefix = "";
    String columnPostfix = "";
    List<ConfigAttribute> allAttributes; //all attributes
    List<ConfigAttribute> excludes; //only exclude attributes
    List<ConfigAttribute> includes; //only include attributes
    //IdentifierMetaData idMetaData;
    
    ConfigSource(){
        //this.idMetaData = idMetaData;
        existingTables = new CaseInsensitiveMap();
        existingAuditTables = new CaseInsensitiveMap();
        allAttributes = new ArrayList<>();
        excludes = new ArrayList<>();
        includes = new ArrayList<>();
    }
    
    void addAtrributes(List<ConfigAttribute> attributes){
        
        for ( ConfigAttribute attrib : attributes){
            
            addAtribute(attrib);
            
        }
    }

    void addAtribute(ConfigAttribute attrib) {

        //TODO handle regexp or wildcards resolve all excludes 1st
        //then resolve includes, all in one step before processing
        //tables
        
        allAttributes.add(attrib);
        
        switch (attrib.getType()) {
            case exclude:
                excludes.add(attrib);
                if (attrib.getTable().isEmpty()){
                    //do not currently handle exclude of column
                    //names only
                }
                else {
                    if (attrib.getColumn().isEmpty()){
                        //exclude table
                        getTableConfig(attrib.getTable()).setExcludeTable(Boolean.FALSE);
                    }
                    else {
                        //exclude specific column
                        getTableConfig(attrib.getTable()).addExcludedColumn(attrib.getColumn());
                    }
                }
                break;
            case include:
                includes.add(attrib);
                if (attrib.getTable().isEmpty()){
                    //do not currently handle include of column
                    //names only
                }
                else {
                    if (attrib.getColumn().isEmpty()){
                        //include table (this is default)
                        getTableConfig(attrib.getTable()).setExcludeTable(Boolean.FALSE);
                    }
                    else {
                        //include specific column (this is default)
                        getTableConfig(attrib.getTable()).addIncludedColumn(attrib.getColumn());
                    }
                }
                break;
            case tableprefix:
                setTablePrefix(attrib.getValue());
                break;
            case tablepostfix:
                setTablePostfix(attrib.getValue());
                break;
            case columnprefix:
                setColumnPrefix(attrib.getValue());
                break;
            case columnpostfix:
                setColumnPostfix(attrib.getValue());
                break;
            case auditinsert:
                getTableConfig(attrib.getTable()).setHasInsertTrigger(attrib.getBooleanValue());
                break;
            case auditupdate:
                getTableConfig(attrib.getTable()).setHasUpdateTrigger(attrib.getBooleanValue());
                break;
            case auditdelete:
                getTableConfig(attrib.getTable()).setHasDeleteTrigger(attrib.getBooleanValue());
                break;
            case unknown:
                break;
        }
    }
    
    void applyAttributes(){
        
        //apply excludes
        //apply includes
        //apply everything else
    }
    
    void addTable(TableDef tableDef){
        
        
    }
    
    void addTables(List<TableDef> tablesDefs){
        
        for ( TableDef tableDef : tablesDefs){
            addTable(tableDef);
        }
        
    }
    
    void addExistingAuditTable (String auditTableName){
        
        if (!existingAuditTables.containsKey(auditTableName)){
            TableConfig atc = new TableConfig(auditTableName);
            existingAuditTables.put (auditTableName, atc);
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
        
        if(!existingTables.containsKey(tableName)){
            TableConfig tc = new TableConfig(tableName);
            existingTables.put(tableName, tc);
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
     * Finds the table in the existing table map and sets the exclude
     * property.
     * 
     * @param tableName The name of the table to exclude from having an audit
     * table created for it.
     */
    void addExcludedTable (String tableName){
         TableConfig tc;
         ensureTableConfig(tableName);
         tc = this.getTableConfig(tableName);
         tc.setExcludeTable(Boolean.TRUE);
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
        
        ensureTableConfig(tableName);
        tc = this.getTableConfig(tableName);
        tc.addExcludedColumn(columnName);
        
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

        ensureTableConfig(tableName);
        tc = this.getTableConfig(tableName);
        tc.addIncludedColumn(columnName);
        
    }

    TableConfig getTableConfig (String tableName){
        
        return existingTables.get(tableName);
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

    //not supported - can wreak havok with table ids
    void setColumnPostfix(String columnPostfix) {
        this.columnPostfix = "";
        //this.columnPostfix = columnPostfix;
    }

    String getColumnPrefix() {
        return columnPrefix;
    }

    //not supported - can wreak havoc with table ids
    void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = "";
        //this.columnPrefix = columnPrefix;
    }

    TableConfig getExistingAuditTable(String key) {
        return existingAuditTables.get(key);
    }
    
}
