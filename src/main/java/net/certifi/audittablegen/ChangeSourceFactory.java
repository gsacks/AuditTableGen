/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.Map;
//import org.apache.commons.collections.map.CaseInsensitiveMap;

/**
 *
 * @author Glenn Sacks
 */
public class ChangeSourceFactory {
    
    ConfigSource configSource;
    
    ChangeSourceFactory (ConfigSource configSource){
    
        this.configSource = configSource;
        
    }
    
    TableChangeSource getAuditTableChangeSource(String baseTableName){

        String auditTableName = configSource.tablePrefix
                    + baseTableName
                    + configSource.tablePostfix;
        
        TableConfig sourceTable = configSource.getTableConfig(baseTableName);
        if (sourceTable == null){
            //table does not exist
            return null;
        }

        TableConfig auditTable = null;
        Boolean isNewTable = true;
        if (configSource.hasExistingAuditTable(auditTableName)){
            auditTable = configSource.getExistingAuditTable(auditTableName);
            isNewTable = false;
            
        }
        
        //if the entire table is excluded, it might be better to return null
        //but for now return an object with the excluded property set, and
        //let the consuming object decide if it needs to do anything or not.
        TableChangeSource ts = new TableChangeSource(isNewTable, auditTableName, sourceTable.getExcludeTable());

        //Map sourceColumns = sourceTable.getColumns();
        for (Map.Entry<String, Map<String, String>> entry : sourceTable.getColumns().entrySet()) {
            //not looking at excluded/included because all columns are included in the audit table
            //exclude/include only applies to the trigger
            String columnName = (entry.getValue().get("COLUMN_NAME"));
            String columnType = (entry.getValue().get("TYPE_NAME"));
            String columnSizeStr = (entry.getValue().get("COLUMN_SIZE"));
            int columnSize;
            try {
                columnSize = Integer.parseInt(columnSizeStr);
            } catch (NumberFormatException e) {
                columnSize = 0;
            }
            String decimalSizeStr = (entry.getValue().get("DECIMAL_SIZE"));
            int decimalSize;
            try {
                decimalSize = Integer.parseInt(decimalSizeStr);
            } catch (NumberFormatException e) {
                decimalSize = 0;
            }
            
            ColumnChangeSource ccs;
            if (isNewTable) {
                //new table, new column
                ccs = new ColumnChangeSource(columnName, columnType, columnSize, decimalSize, entry.getValue());
            } else if (!auditTable.getColumns().containsKey(columnName)) {
                //old table, new column
                ccs = new ColumnChangeSource(columnName, columnType, columnSize, decimalSize, entry.getValue());
            } else {
                //old table, existing column
                Map<String, String> auditColumn = auditTable.getColumns().get(columnName);
                String oldColumnType = auditColumn.get("TYPE_NAME");
                String oldColumnSizeStr = auditColumn.get("COLUMN_SIZE");
                int oldColumnSize;
                try {
                    oldColumnSize = Integer.parseInt(oldColumnSizeStr);
                } catch (NumberFormatException e) {
                    oldColumnSize = 0;
                }
                String oldDecimalSizeStr = auditColumn.get("DECIMAL_SIZE");
                int oldDecimalSize;
                try {
                    oldDecimalSize = Integer.parseInt(oldDecimalSizeStr);
                } catch (NumberFormatException e) {
                    oldDecimalSize = 0;
                }
                
                ccs = new ColumnChangeSource(columnName, columnType, oldColumnType,
                        columnSize, oldColumnSize, decimalSize, oldDecimalSize,
                        entry.getValue(), auditColumn);

            }            
            ts.addColumn(ccs);
            
        }

        return ts;
    }
            
    TriggerChangeSource getAuditTriggerChangeSource (String baseTableName){
        
        String auditTableName = configSource.tablePrefix
                    + baseTableName
                    + configSource.tablePostfix;
        
        TriggerChangeSource triggerChangeSource = new TriggerChangeSource(baseTableName, auditTableName);
        
        TableConfig tc = configSource.getTableConfig(baseTableName);
        
        for (Map.Entry<String, Map<String, String>> entry : tc.getColumns().entrySet() ){
            
            //ToDo: make this search look for regexp
            //ToDo: make this case insensitive
            String columnName = entry.getValue().get("COLUMN_NAME");
            String auditColumnName = configSource.columnPrefix
                    + columnName
                    + configSource.columnPostfix;
            TriggerColumnChangeSource tccs;
            if (!tc.excludedColumns.containsKey(columnName)
                || tc.includedColumns.containsKey(columnName) ){
                //include column in firing trigger
                tccs = new TriggerColumnChangeSource(columnName, auditColumnName, true);
            }
            else {
                //exclude column from firing trigger
                tccs = new TriggerColumnChangeSource(columnName, auditColumnName, false);
            }
            triggerChangeSource.addColumn(tccs);
            
        }
        
        return triggerChangeSource;
        
    }    
        

        
    
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