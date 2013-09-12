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
    
    //Map<String, TableConfig> existingTables;
    //Map<String, TableConfig> existingAuditTables;

    List<TableDef> allTables; //list of all tables in the tartget db/schema
    List<ConfigAttribute> excludes; //only exclude attributes
    List<ConfigAttribute> includes; //only include attributes
    List<ConfigAttribute> dbAttribs; //prefixes & postfixes
    List<ConfigAttribute> triggerAttribs; //triggers
    List<ConfigAttribute> otherAttributes; //these are of unknown type
    //IdentifierMetaData idMetaData;
    
    ConfigSource(){
        //this.idMetaData = idMetaData;
        //existingTables = new CaseInsensitiveMap();
        //existingAuditTables = new CaseInsensitiveMap();
        dbAttribs = new ArrayList<>();
        triggerAttribs = new ArrayList<>();
        excludes = new ArrayList<>();
        includes = new ArrayList<>();
        otherAttributes = new ArrayList<>();
                
        allTables = new ArrayList<>();

    }
    
    void addAttributes(List<ConfigAttribute> attributes){
        
        for ( ConfigAttribute attrib : attributes){
            
            addAttribute(attrib);
            
        }
    }

    void addAttribute(ConfigAttribute attrib) {
        
        switch (attrib.getType()) {
            case exclude:
                excludes.add(attrib);
                break;
            case include:
                includes.add(attrib);
                break;
            case tableprefix: 
            case tablepostfix:
            case columnprefix:
            case columnpostfix:
                dbAttribs.add(attrib);
                break;
            case auditinsert:
            case auditupdate:
            case auditdelete:
                triggerAttribs.add(attrib);
            case unknown:
            default:
                otherAttributes.add(attrib);
                break;
        }
    }
    
   
    
    /**
     * Applies all configuration attributes to the set of tables.  Excludes are
     * processed first, then includes (which may override excludes) and then all
     * other attributes.  If an attribute references a table that does not exist
     * in the table list, then it will not be applied.  Tables should be loaded
     * before applyAttributes is called.
     */
//    void applyAttributes(){
//        
//        for ( ConfigAttribute attrib : excludes){
//            applyAttribute(attrib);
//        }
//        
//        for ( ConfigAttribute attrib : includes){
//            applyAttribute(attrib);
//        }
//        
//        for ( ConfigAttribute attrib : otherAttributes){
//            applyAttribute(attrib);
//        }
//        
//    }
    
    void addTable(TableDef tableDef){
        
        allTables.add(tableDef);
    }
    
    void addTables(List<TableDef> tablesDefs){
        
        allTables.addAll(tablesDefs);
        
    }
    
}
    
    /**
     * Get a List of tables from the Tables map whose keys match the
     * supplied pattern.  The objects in the List are the same objects
     * in the map, and changes to the List are reflected in the Map.  Add
     * or remove from the List has no effect on the Map.
     * 
     * ONLY EXACT MATCHES ARE CURRENTLY SUPPORTED, EXCEPT EMPTY STRING OR '*'
     * MATCHES ALL TABLES.
     * 
     * @param tablePattern table name pattern to match on
     * @return List of TableConfig objects matching the pattern 
     */
//    List getTableMatches(String tablePattern){
//        
//        List<TableConfig> matched = new ArrayList<>();
//        
//        Iterator<Entry<String, TableConfig>> iter  = existingAuditTables.entrySet().iterator();
//        while (iter.hasNext()) {
//            TableConfig tc = iter.next().getValue();
//            
//            if (tablePattern.isEmpty()
//                    || tablePattern.equals("*")
//                    || tablePattern.equalsIgnoreCase(tc.tableName)){
//                matched.add(tc);
//            }
//        }
//        
//        return matched;
//        
//    }
    
    /**
     * Get a List of columns from the Tables map whose keys match the
     * supplied pattern.  The objects in the List are the same objects
     * in the map, and changes to the List are reflected in the Map.  Add
     * or remove from the List has no effect on the Map.
     * 
     * ONLY EXACT MATCHES ARE CURRENTLY SUPPORTED, EXCEPT EMPTY STRING OR '*'
     * MATCHES ALL TABLES.
     * 
     * @param tablePattern table name pattern to match on
     * @return List of TableConfig objects matching the pattern 
     */
    
//    List getColumnMatches(Map<String, Str> c, String columnPattern){
//        
//        List<TableConfig> matched = new ArrayList<>();
//        
//        Iterator<Entry<String, TableConfig>> iter  = existingAuditTables.entrySet().iterator();
//        while (iter.hasNext()) {
//            TableConfig tc = iter.next().getValue();
//            
//            if (tablePattern.isEmpty()
//                    || tablePattern.equals("*")
//                    || tablePattern.equalsIgnoreCase(tc.tableName)){
//                matched.add(tc);
//            }
//        }
//        
//        return matched;
//        
//    }
    
//    void addExistingAuditTable (String auditTableName){
//        
//        if (!existingAuditTables.containsKey(auditTableName)){
//            TableConfig atc = new TableConfig(auditTableName);
//            existingAuditTables.put (auditTableName, atc);
//        }
//
//    }
    
//    Boolean hasExistingAuditTable (String auditTableName){
//        
//        return existingAuditTables.containsKey(auditTableName);
//    }
    
    /**
     * Add table to the map of database tables.  Note that the table
     * name is stored in the TableConfig object case sensitive, but the
     * map key is case insensitive.  If the table already exists, it will
     * not be replaced.
     * 
     * @param tableName 
     */
//    void addTableConfig (String tableName){
//        
//        if(!existingTables.containsKey(tableName)){
//            TableConfig tc = new TableConfig(tableName);
//            existingTables.put(tableName, tc);
//        }
//    }
    
    
    /**
     * A proxy for addTableConfig
     * 
     * @param tableName 
     */
//    void ensureTableConfig (String tableName){
//        addTableConfig(tableName);
//    }
    
    /**
     * Finds the table in the existing table map and sets the exclude
     * property.
     * 
     * @param tableName The name of the table to exclude from having an audit
     * table created for it.
     */
//    void addExcludedTable (String tableName){
//         TableConfig tc;
//         ensureTableConfig(tableName);
//         tc = this.getTableConfig(tableName);
//         tc.setExcludeTable(Boolean.TRUE);
//    }
    /**
     * Finds the table in the existing table map and adds the column
     * to the list of columns to be excluded from triggering an update
     * to the audit table.  Note that the case sensitive table and column
     * name are stored, but the key lookup is case insensitive.
     * 
     * @param tableName The name of the table containing the column
     * @param columnName The column name.
     */
//    void addExcludedColumn (String tableName, String columnName){
//    
//        TableConfig tc;
//        
//        ensureTableConfig(tableName);
//        tc = this.getTableConfig(tableName);
//        tc.addExcludedColumn(columnName);
//        
//    }
    
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
//    void addIncludedColumn (String tableName, String columnName){
//    
//        TableConfig tc;
//
//        ensureTableConfig(tableName);
//        tc = this.getTableConfig(tableName);
//        tc.addIncludedColumn(columnName);
//        
//    }

//    TableConfig getTableConfig (String tableName){
//        
//        return existingTables.get(tableName);
//    }
//    
//    String getTablePostfix() {
//        return tablePostfix;
//    }
//
//    void setTablePostfix(String tablePostfix) {
//        this.tablePostfix = tablePostfix;
//    }
//
//    String getTablePrefix() {
//        return tablePrefix;
//    }
//
//    void setTablePrefix(String tablePrefix) {
//        this.tablePrefix = tablePrefix;
//    }
//
//    String getColumnPostfix() {
//        return columnPostfix;
//    }
//
//    //not supported - can wreak havok with table ids
//    void setColumnPostfix(String columnPostfix) {
//        this.columnPostfix = "";
//        //this.columnPostfix = columnPostfix;
//    }
//
//    String getColumnPrefix() {
//        return columnPrefix;
//    }
//
//    //not supported - can wreak havoc with table ids
//    void setColumnPrefix(String columnPrefix) {
//        this.columnPrefix = "";
//        //this.columnPrefix = columnPrefix;
//    }
//
//    TableConfig getExistingAuditTable(String key) {
//        return existingAuditTables.get(key);
//    }
//    
//}
