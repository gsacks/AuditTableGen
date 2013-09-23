/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.*;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.commons.collections.map.CaseInsensitiveMap;

/**
 *
 * @author Glenn Sacks
 */
public class ChangeSourceFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(ChangeSourceFactory.class);
    ConfigSource configSource;
    Map<String, TableDef> auditTablesMap;
    Map<String, TableDef> baseTableMap;
    List<TableDef> baseTableList;
    String tablePrefix = "zz_";
    String tablePostfix = "";
    String columnPrefix = "zz_";
    String columnPostfix = "";
    
    ChangeSourceFactory (ConfigSource configSource){
    
        this.configSource = configSource;
        auditTablesMap = new CaseInsensitiveMap();
        baseTableMap = new CaseInsensitiveMap();
        baseTableList = new ArrayList<>();
        
        for (ConfigAttribute attrib : configSource.dbAttribs) {
            switch (attrib.getType()) {
            case tableprefix:
                tablePrefix = attrib.getValue();
                break;
            case tablepostfix:
                tablePostfix = attrib.getValue();
                break;
            case columnprefix:
                columnPrefix = attrib.getValue();
                break;
            case columnpostfix:
                columnPostfix = attrib.getValue();
                break;
            default:
                break;
            }
        }
        
        for (TableDef td : configSource.allTables ){
            
            String tableName = td.getName();
            if (tableName.toLowerCase().startsWith(tablePrefix.toLowerCase())
                    && tableName.toLowerCase().endsWith(tablePostfix.toLowerCase())){
                //matches audit table pattern
                auditTablesMap.put(tableName, td);
            }
            else {
                baseTableList.add(td);
                baseTableMap.put(tableName, td);
            }
        }
        
        
    }
    
    /**
     * Compares the value of str to the pattern represented by pattern. pattern
     * may be a simple string, or may contain wildcards or a regular expression
     * in a future implementation. CURRENTLY ONLY SUPPORTS case insensitive
     * string compare, always matches on the wildcard '*' or an empty string in
     * pattern.
     *
     * @param str string being compared
     * @param pattern string or expression to compare to
     * @return
     */
    Boolean isPatternMatch(String str, String pattern) {

        //TODO: this is where regexp or wildcard pattern matching should go
        if (pattern.isEmpty()
                || pattern.equals("*")
                || pattern.toLowerCase().equals(str.toLowerCase())) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

    Boolean isTableExcluded(String tableName) {

        Boolean result = Boolean.FALSE;

        for (ConfigAttribute attrib : configSource.excludes) {
            if (attrib.getColumnName().isEmpty()
                    && isPatternMatch(tableName, attrib.getTableName())) {
                result = Boolean.TRUE;
            }
        }

        if (result) {
            //check for include -> overrides exclude
            for (ConfigAttribute attrib : configSource.includes) {
                if (attrib.getColumnName().isEmpty()
                        && isPatternMatch(tableName, attrib.getTableName())) {
                    result = Boolean.FALSE;
                }
            }
        }
        
        return result;
        
    }
    
    Boolean isColumnExcluded(String tableName, String columnName) {
        
        Boolean result = Boolean.FALSE;
        
        for (ConfigAttribute attrib : configSource.excludes) {
            if (isPatternMatch(columnName, attrib.getColumnName())
                    && isPatternMatch(tableName, attrib.getTableName())) {
                result = Boolean.TRUE;
            }
        }

        if (result) {
            //check for include -> overrides exclude
            for (ConfigAttribute attrib : configSource.includes) {
                if (isPatternMatch(columnName, attrib.getColumnName())
                        && isPatternMatch(tableName, attrib.getTableName())) {
                    result = Boolean.FALSE;
                }
            }
        }
        
        return result;
    }
    
    Boolean hasTriggerType(String tableName, ConfigAttributeTypes type){
        
        Boolean result = Boolean.TRUE;
        for (ConfigAttribute attrib : configSource.triggerAttribs) {
            if (tableName.equalsIgnoreCase(attrib.getTableName())){
                if (attrib.getType().equals(type))
                    result = attrib.getBooleanValue();
            }
        }
        return result;
    }
    
    List<DBChangeUnit> getDBChangeList(TableDef baseTableDef){
        
        List<DBChangeUnit> tableChangeUnits = new ArrayList();
        List<DBChangeUnit> tableRenameColumns = new ArrayList();
        DBChangeUnit workUnit;
        String baseTableName = baseTableDef.getName();
        
        String newColumnName;
        
        if (baseTableDef == null ){
            logger.error("Invalid input. null TableDef");
            return tableChangeUnits;
        }
        
        if (baseTableDef.getColumns().isEmpty()){
            logger.error("Invalid Input. TableDef has no columns.");
            return tableChangeUnits;
        }
        
        String auditTableName = tablePrefix
                + baseTableName
                + tablePostfix;
        
        String auditActionColumn = columnPrefix
                + "action"
                + columnPostfix;
        
       String auditTimeStampColumn = columnPrefix
                + "ts"
                + columnPostfix;
                
        String auditUserColumn = columnPrefix
                + "userId"
                + columnPostfix;
        
        TableDef auditTableDef = null;
        if (auditTablesMap.containsKey(auditTableName)){
            auditTableDef = auditTablesMap.get(auditTableName);
        }
        
        if (isTableExcluded(baseTableName)){
            //drop all audit triggers
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.begin));
            workUnit = new DBChangeUnit(DBChangeType.dropTriggers);
            workUnit.setTableName(baseTableName);
            tableChangeUnits.add(workUnit);
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
            
            //done.  We don't want to alter any exising audit table
            return tableChangeUnits;
        }
        
        //create or alter audit table
        if (null == auditTableDef){
            //create table
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.begin));
            workUnit = new DBChangeUnit(DBChangeType.createTable);
            workUnit.tableName = auditTableName;
            tableChangeUnits.add(workUnit);
            
            //create id column
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditTableName + "Id");
            workUnit.setTableName(auditTableName);
            workUnit.setDataType("integer");
            workUnit.setIdentity(Boolean.TRUE);
            tableChangeUnits.add(workUnit);
            
            //create all columns on the base table
            for (ColumnDef baseColumn : baseTableDef.getColumns()) {
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(baseColumn.getName());
                workUnit.setTableName(auditTableName);
                workUnit.setDataType(baseColumn.getType());
                workUnit.setSize(baseColumn.getSize());
                workUnit.setDecimalSize(baseColumn.getDecimalSize());
                tableChangeUnits.add(workUnit);
            }
            
            //create the audit tracking columns
            //action
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditActionColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setDataType("char"); //insert, update, or delete
            workUnit.setSize(6);
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);
            
            //user
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditUserColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setDataType("char");
            workUnit.setSize(configSource.getMaxUserNameLength());
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);

            //timestamp
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditTimeStampColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setDataType("timestamp");
            workUnit.setSize(0);
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);
            
            //end of table
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
        }
        else {
            //alter table
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.begin));
            workUnit = new DBChangeUnit(DBChangeType.alterTable);
            workUnit.setTableName(auditTableName);
            tableChangeUnits.add(workUnit);
            
            //to make this a little easier, get a map for the column list
            Map<String, ColumnDef> auditColumnMap = new HashMap<>();
            for ( ColumnDef auditColumn : auditTableDef.getColumns()){
                auditColumnMap.put(auditColumn.getName(), auditColumn);
            }

            //make sure the audit columns and the id exist
            //create the audit tracking columns
            //action
            if (!auditColumnMap.containsKey(auditActionColumn)){
                logger.error ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditActionColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditActionColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setDataType("char"); //insert, update, or delete
                workUnit.setSize(6);
                workUnit.setDecimalSize(0);
                tableChangeUnits.add(workUnit);
            }
            
            //user
            if (!auditColumnMap.containsKey(auditUserColumn)){
                logger.error ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditUserColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditUserColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setDataType("char");
                workUnit.setSize(configSource.getMaxUserNameLength());
                workUnit.setDecimalSize(0);
                tableChangeUnits.add(workUnit);
            }
            
            //timestamp
            if (!auditColumnMap.containsKey(auditTimeStampColumn)){            
                logger.error ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditTimeStampColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditTimeStampColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setDataType("timestamp");
                workUnit.setSize(0);
                workUnit.setDecimalSize(0);
                tableChangeUnits.add(workUnit);
            }
            
            //add or alter columns
            for ( ColumnDef baseColumn : baseTableDef.getColumns()){
                if (auditColumnMap.containsKey(baseColumn.name)){
                    //existing column
                    ColumnDef auditColumn = auditColumnMap.get(baseColumn.name);
                    if (auditColumn.getType().equals(baseColumn.getType())
                            && auditColumn.getSize() >= baseColumn.getSize()
                            && auditColumn.getDecimalSize() >= baseColumn.getDecimalSize()){
                        //nothing to do
                    }
                    else if (auditColumn.getType().equals(baseColumn.getType())
                            && (auditColumn.getSize() < baseColumn.getSize()
                                || auditColumn.getDecimalSize() < baseColumn.getDecimalSize()) ) {
                        //type is the same, but size increased
                        workUnit = new DBChangeUnit(DBChangeType.alterColumnSize);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(baseColumn.getName());
                        workUnit.setDataType(baseColumn.getType());
                        workUnit.setSize(baseColumn.getSize());
                        workUnit.setDecimalSize(baseColumn.getDecimalSize());
                        tableChangeUnits.add(workUnit);
                    }
                    else {
                        //type changes or size shrunk. Rename existing column
                        //and create new column in its place.  This requires
                        //the column rename to be done as a sepereate command
                        //from the other column changes.  (At least it does on
                        //postgres.
                        int i = 1;
                        do {
                            newColumnName = String.format("%s_prev%d", auditColumn.getName(), i);
                            i++;
                        } while (auditColumnMap.containsKey(newColumnName));
                        //rename the old version of the audit column
                        tableRenameColumns.add((new DBChangeUnit(DBChangeType.begin)));
                        workUnit = new DBChangeUnit(DBChangeType.alterTable);
                        workUnit.setTableName(auditTableName);
                        tableRenameColumns.add(workUnit);
                        workUnit = new DBChangeUnit(DBChangeType.alterColumnName);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(auditColumn.getName());
                        workUnit.setNewColumnName(newColumnName);
                        workUnit.setDataType(auditColumn.getType());
                        workUnit.setSize(auditColumn.getSize());
                        workUnit.setDecimalSize(auditColumn.getDecimalSize());
                        tableRenameColumns.add(workUnit);
                        tableRenameColumns.add((new DBChangeUnit(DBChangeType.end)));
                        
                        //now add the new version of the column
                        workUnit = new DBChangeUnit(DBChangeType.addColumn);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(baseColumn.getName());
                        workUnit.setDataType(baseColumn.getType());
                        workUnit.setSize(baseColumn.getSize());
                        workUnit.setDecimalSize(baseColumn.getDecimalSize());
                        tableChangeUnits.add(workUnit);         
                    }                        
                }
                else {
                    //new column
                    workUnit = new DBChangeUnit(DBChangeType.addColumn);
                    workUnit.setTableName(auditTableName);
                    workUnit.setColumnName(baseColumn.getName());
                    workUnit.setDataType(baseColumn.getType());
                    workUnit.setSize(baseColumn.getSize());
                    workUnit.setDecimalSize(baseColumn.getDecimalSize());
                    tableChangeUnits.add(workUnit);
                }
            }
            
            //end of table
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
        }
        
        //begin trigger changes
        tableChangeUnits.add(new DBChangeUnit(DBChangeType.begin));
        workUnit = new DBChangeUnit(DBChangeType.createTriggers);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        tableChangeUnits.add(workUnit);
        
        //insert trigger
        workUnit = new DBChangeUnit(DBChangeType.fireOnInsert);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        workUnit.setFiresTrigger(hasTriggerType(baseTableName, ConfigAttributeTypes.auditinsert));
        tableChangeUnits.add(workUnit);

        //update trigger
        workUnit = new DBChangeUnit(DBChangeType.fireOnUpdate);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        workUnit.setFiresTrigger(hasTriggerType(baseTableName, ConfigAttributeTypes.auditupdate));
        tableChangeUnits.add(workUnit);

        //delete trigger
        workUnit = new DBChangeUnit(DBChangeType.fireOnDelete);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        workUnit.setFiresTrigger(hasTriggerType(baseTableName, ConfigAttributeTypes.auditdelete));
        tableChangeUnits.add(workUnit);

        //now add the columns that will be included in the trigger
        //and set whether or not they will cause it to fire (default is true)
        for (ColumnDef baseColumnDef : baseTableDef.getColumns()){
            workUnit = new DBChangeUnit(DBChangeType.addTriggerColumn);
            workUnit.setTableName(baseTableName);
            workUnit.setAuditTableName(auditTableName);
            workUnit.setColumnName(baseColumnDef.getName());
            if (isColumnExcluded(baseTableDef.getName(), baseColumnDef.getName())){
                workUnit.setFiresTrigger(Boolean.FALSE);
            }
            tableChangeUnits.add(workUnit);
        }
        
        //add the audit tracking columns

        //action
        workUnit = new DBChangeUnit(DBChangeType.addTriggerAction);
        workUnit.setColumnName(auditActionColumn);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        tableChangeUnits.add(workUnit);

        //user
        workUnit = new DBChangeUnit(DBChangeType.addTriggerUser);
        workUnit.setColumnName(auditUserColumn);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        tableChangeUnits.add(workUnit);

        //timestamp
        workUnit = new DBChangeUnit(DBChangeType.addTriggerTimeStamp);
        workUnit.setColumnName(auditTimeStampColumn);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        tableChangeUnits.add(workUnit);
        
        //end trigger changes
        tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
        
        if ( tableRenameColumns.isEmpty()){
            return tableChangeUnits;
        }
        else {
            //put column rename(s) at the front of the list
            tableRenameColumns.addAll(tableChangeUnits);
            return tableRenameColumns;
        }
        
    }
    
    List<DBChangeUnit> getDBChangeList(){
        
        List<DBChangeUnit> dbChangeUnits = new ArrayList();
        
        for ( TableDef td : baseTableList) {
            List<DBChangeUnit> tableChangeUnits = getDBChangeList(td);
            dbChangeUnits.addAll(tableChangeUnits);
        }
        
        return dbChangeUnits;
        
    }
    
    List<DBChangeUnit> getDBChangeList(String tableName){
        
        if (baseTableMap.containsKey(tableName)){
            return getDBChangeList(baseTableMap.get(tableName));
        }
        else {
            //not found.  Return empty list
            return new ArrayList<DBChangeUnit>();
        }
    }
    
//     void applyAttribute(ConfigAttribute attrib){
//
//        //TODO handle regexp or wildcards resolve all excludes 1st
//        //then resolve includes, all in one step before processing
//        //tables
//        
//        switch (attrib.getType()) {
//            case exclude:
//
//                if (attrib.getTableName().isEmpty()){
//                    //do not currently handle exclude of column
//                    //names only
//                }
//                else {
//                    if (attrib.getColumnName().isEmpty()){
//                        //exclude table
//                        getTableConfig(attrib.getTableName()).setExcludeTable(Boolean.FALSE);
//                    }
//                    else {
//                        //exclude specific column
//                        getTableConfig(attrib.getTableName()).addExcludedColumn(attrib.getColumnName());
//                    }
//                }
//                break;
//            case include:
//                if (attrib.getColumnName().isEmpty()){
//                    //attribute applies to entire table
//                    
//                }
//                
//                
//                if (attrib.getTableName().isEmpty()){
//                    Iterator iter = existingAuditTables.entrySet().iterator();
//                    //do not currently handle include of column
//                    //names only
//                }
//                else {
//                    if (attrib.getColumnName().isEmpty()){
//                        //include table (this is default)
//                        getTableConfig(attrib.getTableName()).setExcludeTable(Boolean.FALSE);
//                    }
//                    else {
//                        //include specific column (this is default)
//                        getTableConfig(attrib.getTableName()).addIncludedColumn(attrib.getColumnName());
//                    }
//                }
//                break;
//           
//            case auditinsert:
//                if (existingTables.containsKey(attrib.tableName)){
//                    getTableConfig(attrib.getTableName()).setHasInsertTrigger(attrib.getBooleanValue());
//                }                
//                break;
//            case auditupdate:
//                if (existingTables.containsKey(attrib.tableName)){
//                    getTableConfig(attrib.getTableName()).setHasUpdateTrigger(attrib.getBooleanValue());
//                }
//                break;
//            case auditdelete:
//                if (existingTables.containsKey(attrib.tableName)){
//                    getTableConfig(attrib.getTableName()).setHasDeleteTrigger(attrib.getBooleanValue());
//                }
//                break;
//            case unknown:
//                break;
//        }
//    }
     
    
//    TableChangeSource getAuditTableChangeSource(String baseTableName){
//
//        String auditTableName = configSource.tablePrefix
//                    + baseTableName
//                    + configSource.tablePostfix;
//        
//        TableConfig sourceTable = configSource.getTableConfig(baseTableName);
//        if (sourceTable == null){
//            //table does not exist
//            return null;
//        }
//
//        TableConfig auditTable = null;
//        Boolean isNewTable = true;
//        if (configSource.hasExistingAuditTable(auditTableName)){
//            auditTable = configSource.getExistingAuditTable(auditTableName);
//            isNewTable = false;
//            
//        }
//        
//        //if the entire table is excluded, it might be better to return null
//        //but for now return an object with the excluded property set, and
//        //let the consuming object decide if it needs to do anything or not.
//        TableChangeSource ts = new TableChangeSource(isNewTable, auditTableName, sourceTable.getExcludeTable());
//
//        //Map sourceColumns = sourceTable.getColumns();
//        for (Map.Entry<String, Map<String, String>> entry : sourceTable.getColumns().entrySet()) {
//            //not looking at excluded/included because all columns are included in the audit table
//            //exclude/include only applies to the trigger
//            String columnName = (entry.getValue().get("COLUMN_NAME"));
//            String columnType = (entry.getValue().get("TYPE_NAME"));
//            String columnSizeStr = (entry.getValue().get("COLUMN_SIZE"));
//            int columnSize;
//            try {
//                columnSize = Integer.parseInt(columnSizeStr);
//            } catch (NumberFormatException e) {
//                columnSize = 0;
//            }
//            String decimalSizeStr = (entry.getValue().get("DECIMAL_SIZE"));
//            int decimalSize;
//            try {
//                decimalSize = Integer.parseInt(decimalSizeStr);
//            } catch (NumberFormatException e) {
//                decimalSize = 0;
//            }
//            
//            ColumnChangeSource ccs;
//            if (isNewTable) {
//                //new table, new column
//                ccs = new ColumnChangeSource(columnName, columnType, columnSize, decimalSize, entry.getValue());
//            } else if (!auditTable.getColumns().containsKey(columnName)) {
//                //old table, new column
//                ccs = new ColumnChangeSource(columnName, columnType, columnSize, decimalSize, entry.getValue());
//            } else {
//                //old table, existing column
//                Map<String, String> auditColumn = auditTable.getColumns().get(columnName);
//                String oldColumnType = auditColumn.get("TYPE_NAME");
//                String oldColumnSizeStr = auditColumn.get("COLUMN_SIZE");
//                int oldColumnSize;
//                try {
//                    oldColumnSize = Integer.parseInt(oldColumnSizeStr);
//                } catch (NumberFormatException e) {
//                    oldColumnSize = 0;
//                }
//                String oldDecimalSizeStr = auditColumn.get("DECIMAL_SIZE");
//                int oldDecimalSize;
//                try {
//                    oldDecimalSize = Integer.parseInt(oldDecimalSizeStr);
//                } catch (NumberFormatException e) {
//                    oldDecimalSize = 0;
//                }
//                
//                ccs = new ColumnChangeSource(columnName, columnType, oldColumnType,
//                        columnSize, oldColumnSize, decimalSize, oldDecimalSize,
//                        entry.getValue(), auditColumn);
//
//            }            
//            ts.addColumn(ccs);
//            
//        }
//
//        return ts;
//    }
            
//    TriggerChangeSource getAuditTriggerChangeSource (String baseTableName){
//        
//        String auditTableName = configSource.tablePrefix
//                    + baseTableName
//                    + configSource.tablePostfix;
//        
//        TriggerChangeSource triggerChangeSource = new TriggerChangeSource(baseTableName, auditTableName);
//        
//        TableConfig tc = configSource.getTableConfig(baseTableName);
//        
//        for (Map.Entry<String, Map<String, String>> entry : tc.getColumns().entrySet() ){
//            
//            //ToDo: make this search look for regexp
//            //ToDo: make this case insensitive
//            String columnName = entry.getValue().get("COLUMN_NAME");
//            String auditColumnName = configSource.columnPrefix
//                    + columnName
//                    + configSource.columnPostfix;
//            TriggerColumnChangeSource tccs;
//            if (!tc.excludedColumns.containsKey(columnName)
//                || tc.includedColumns.containsKey(columnName) ){
//                //include column in firing trigger
//                tccs = new TriggerColumnChangeSource(columnName, auditColumnName, true);
//            }
//            else {
//                //exclude column from firing trigger
//                tccs = new TriggerColumnChangeSource(columnName, auditColumnName, false);
//            }
//            triggerChangeSource.addColumn(tccs);
//            
//        }
//        
//        return triggerChangeSource;
//        
//    }    
      
    
}

//for reference - this is the result set info for DatabaseMetaData.getColumns
//Each column description has the following columns:
//
//TABLE_CAT String => table catalog (may be null)
//TABLE_SCHEM String => table schema (may be null)
//TABLE_NAME String => table name
//COLUMN_NAME String => column name
//DATA_TYPE int => SQL type from java.sql.Types
//TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
//COLUMN_SIZE int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
//BUFFER_LENGTH is not used.
//DECIMAL_DIGITS int => the number of fractional digits
//NUM_PREC_RADIX int => Radix (typically either 10 or 2)
//NULLABLE int => is NULL allowed.
//columnNoNulls - might not allow NULL values
//columnNullable - definitely allows NULL values
//columnNullableUnknown - nullability unknown
//REMARKS String => comment describing column (may be null)
//COLUMN_DEF String => default value (may be null)
//SQL_DATA_TYPE int => unused
//SQL_DATETIME_SUB int => unused
//CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
//ORDINAL_POSITION int	=> index of column in table (starting at 1)
//IS_NULLABLE String => "NO" means column definitely does not allow NULL values; "YES" means the column might allow NULL values. An empty string means nobody knows.
//SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
//SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
//SCOPE_TABLE String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
//SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)