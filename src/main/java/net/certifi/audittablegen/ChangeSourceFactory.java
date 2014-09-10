/*    Copyright 2014 Certifi Inc.
 *
 *    This file is part of AuditTableGen.
 *
 *        AuditTableGen is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        (at your option) any later version.
 *
 *        AuditTableGen is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with AuditTableGen.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.certifi.audittablegen;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
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
    String auditIdTypeName = "";
    Integer auditIdDefaultDataType = java.sql.Types.BIGINT;
    String auditUserTypeName = "";
    Integer auditUserDefaultDataType = java.sql.Types.CHAR;
    String auditTimeStampTypeName = "";
    Integer auditTimeStampDefaultDataType = java.sql.Types.TIMESTAMP;
    String auditActionTypeName = "";
    Integer auditActionDefaultDataType = java.sql.Types.CHAR;
    
    String sessionUserSQL = "";
    String sessionUserTypeName = "";
    Integer sessionUserDefaultDataType = java.sql.Types.CHAR;
    Integer sessionUserDataSize = 0; //must be set if dataType requires a size
    
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
            case iddatatype:
                auditIdTypeName = attrib.getValue();
					break;
            case userdatatype:      
                auditUserTypeName = attrib.getValue();
                break;
            case timestampdatatype:
                auditTimeStampTypeName = attrib.getValue();
                break;
            case actiondatatype:
                auditActionTypeName = attrib.getValue();
                break;
            case sessionusersql:
                sessionUserSQL = attrib.getValue();
                break;
            case sessionuserdatatype:
                sessionUserTypeName = attrib.getValue();
                break;
            case sessionuserdatasize:
                sessionUserDataSize = attrib.getIntValue();
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
                || pattern.toLowerCase().equals(str.toLowerCase()) ) {
            return Boolean.TRUE;
        }

		  try {
				Pattern p = Pattern.compile( pattern, Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE );
				if ( p != null && p.matcher( str ).matches() ) return Boolean.TRUE;
		  
		  } catch( IllegalArgumentException x) {
			  
			  logger.warn( "Invalid Regexp " + x.getMessage() );
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
        List<DBChangeUnit> renameColumnChangeUnits = new ArrayList();
        List<DBChangeUnit> alterTableChangeUnits = new ArrayList();
        
        DBChangeUnit workUnit;
        String baseTableName = baseTableDef.getName();
        
        String newColumnName;
        
        if (baseTableDef == null ){
            logger.error("Invalid input. null TableDef");
            return tableChangeUnits;
        }
        
        if (baseTableDef.getColumns().isEmpty()){
            ContextedRuntimeException e = new ContextedRuntimeException();
            e.setContextValue("tableName", baseTableDef.getName());
            logger.error("Invalid Input. TableDef has no columns.", e);
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
        
        String sessionUserColumn = columnPrefix
                + "sessionUser"
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
            workUnit.setTypeName(auditIdTypeName);
            workUnit.setIdentity(Boolean.TRUE);
            tableChangeUnits.add(workUnit);
            
            //create all columns on the base table
            for (ColumnDef baseColumn : baseTableDef.getColumns()) {
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(baseColumn.getName());
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(baseColumn.getTypeName());
                workUnit.setSize(baseColumn.getSize());
                workUnit.setDecimalSize(baseColumn.getDecimalSize());
                tableChangeUnits.add(workUnit);
            }
            
            //create the audit tracking columns
            //action
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditActionColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setTypeName(auditActionTypeName); //insert, update, or delete
            workUnit.setSize(6);
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);
            
            //user
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditUserColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setTypeName(auditUserTypeName);
            workUnit.setSize(configSource.getMaxUserNameLength());
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);

            //timestamp
            workUnit = new DBChangeUnit(DBChangeType.addColumn);
            workUnit.setColumnName(auditTimeStampColumn);
            workUnit.setTableName(auditTableName);
            workUnit.setTypeName(auditTimeStampTypeName);
            workUnit.setSize(0);
            workUnit.setDecimalSize(0);
            tableChangeUnits.add(workUnit);
            
            //sessionUser
            if (!sessionUserSQL.isEmpty()) {
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(sessionUserColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(sessionUserTypeName);
                workUnit.setSize(sessionUserDataSize);
                workUnit.setDecimalSize(0);
                tableChangeUnits.add(workUnit);
            }

            //end of table
            tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
				
				// populate the table
				tableChangeUnits.add( new DBChangeUnit(DBChangeType.begin) );
				workUnit = new DBChangeUnit(DBChangeType.fillAuditTable);
            workUnit.tableName = auditTableName;
            tableChangeUnits.add(workUnit);
				
				//   fill all columns on the base table
            for (ColumnDef baseColumn : baseTableDef.getColumns()) {
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(baseColumn.getName());
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(baseColumn.getTypeName());
                workUnit.setSize(baseColumn.getSize());
                workUnit.setDecimalSize(baseColumn.getDecimalSize());
                tableChangeUnits.add(workUnit);
            }
				
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
        
        //sessionuser
        if (!sessionUserSQL.isEmpty()) {
            workUnit = new DBChangeUnit(DBChangeType.addTriggerSessionUser);
            workUnit.setColumnName(sessionUserColumn);
            workUnit.setTableName(baseTableName);
            workUnit.setAuditTableName(auditTableName);
            tableChangeUnits.add(workUnit);
        }
		  
		  workUnit = new DBChangeUnit(DBChangeType.end);
        workUnit.setTableName(baseTableName);
        workUnit.setAuditTableName(auditTableName);
        tableChangeUnits.add(workUnit);
		  
        }  else {
            //alter table
            //there might not be any changes, so store up any changes in 
            //a temporary list, and evaluate.
            alterTableChangeUnits.add(new DBChangeUnit(DBChangeType.begin));
            workUnit = new DBChangeUnit(DBChangeType.alterTable);
            workUnit.setTableName(auditTableName);
            alterTableChangeUnits.add(workUnit);
            
            //to make this a little easier, get a map for the column list
            Map<String, ColumnDef> auditColumnMap = new CaseInsensitiveMap();
            for ( ColumnDef auditColumn : auditTableDef.getColumns()){
                auditColumnMap.put(auditColumn.getName(), auditColumn);
            }

            //make sure the audit columns and the id exist
            //create the audit tracking columns
            //action
            if (!auditColumnMap.containsKey(auditActionColumn)){
                logger.warn ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditActionColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditActionColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(auditActionTypeName); //insert, update, or delete
                workUnit.setSize(6);
                workUnit.setDecimalSize(0);
                alterTableChangeUnits.add(workUnit);
            }
            
            //user
            if (!auditColumnMap.containsKey(auditUserColumn)){
                logger.warn ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditUserColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditUserColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(auditUserTypeName);
                workUnit.setSize(configSource.getMaxUserNameLength());
                workUnit.setDecimalSize(0);
                alterTableChangeUnits.add(workUnit);
            }
            
            //timestamp
            if (!auditColumnMap.containsKey(auditTimeStampColumn)){            
                logger.warn ("Existing audit table {} does not contain column {}. Creating", auditTableName, auditTimeStampColumn );
                workUnit = new DBChangeUnit(DBChangeType.addColumn);
                workUnit.setColumnName(auditTimeStampColumn);
                workUnit.setTableName(auditTableName);
                workUnit.setTypeName(auditTimeStampTypeName);
                workUnit.setSize(0);
                workUnit.setDecimalSize(0);
                alterTableChangeUnits.add(workUnit);
            }
            
            //seesionuser
            if (!sessionUserSQL.isEmpty()) {
                if (!auditColumnMap.containsKey(sessionUserColumn)) {
                    logger.warn("Existing audit table {} does not contain column {}. Creating", auditTableName, sessionUserColumn);
                    workUnit = new DBChangeUnit(DBChangeType.addColumn);
                    workUnit.setColumnName(sessionUserColumn);
                    workUnit.setTableName(auditTableName);
                    workUnit.setTypeName(sessionUserTypeName);
                    workUnit.setSize(sessionUserDataSize);
                    workUnit.setDecimalSize(0);
                    alterTableChangeUnits.add(workUnit);
                }
            }
            
            //add or alter columns
            for ( ColumnDef baseColumn : baseTableDef.getColumns()){
                if (auditColumnMap.containsKey(baseColumn.name)){
                    //existing column
                    ColumnDef auditColumn = auditColumnMap.get(baseColumn.name);
                    if (auditColumn.getTypeName().equalsIgnoreCase(baseColumn.getTypeName())
                            && auditColumn.getSize() >= baseColumn.getSize()
                            && auditColumn.getDecimalSize() >= baseColumn.getDecimalSize()){
                        //nothing to do
                    }
                    else if (auditColumn.getTypeName().equalsIgnoreCase(baseColumn.getTypeName())
                            && (auditColumn.getSize() < baseColumn.getSize()
                                || auditColumn.getDecimalSize() < baseColumn.getDecimalSize()) ) {
                        //type is the same, but size increased
                        workUnit = new DBChangeUnit(DBChangeType.alterColumnSize);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(baseColumn.getName());
                        workUnit.setTypeName(baseColumn.getTypeName());
                        workUnit.setSize(baseColumn.getSize());
                        workUnit.setDecimalSize(baseColumn.getDecimalSize());
                        alterTableChangeUnits.add(workUnit);
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
                        renameColumnChangeUnits.add((new DBChangeUnit(DBChangeType.begin)));
                        workUnit = new DBChangeUnit(DBChangeType.alterTable);
                        workUnit.setTableName(auditTableName);
                        renameColumnChangeUnits.add(workUnit);
                        workUnit = new DBChangeUnit(DBChangeType.alterColumnName);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(auditColumn.getName());
                        workUnit.setNewColumnName(newColumnName);
                        workUnit.setTypeName(auditColumn.getTypeName());
                        workUnit.setSize(auditColumn.getSize());
                        workUnit.setDecimalSize(auditColumn.getDecimalSize());
                        renameColumnChangeUnits.add(workUnit);
                        renameColumnChangeUnits.add((new DBChangeUnit(DBChangeType.end)));
                        
                        //now add the new version of the column
                        workUnit = new DBChangeUnit(DBChangeType.addColumn);
                        workUnit.setTableName(auditTableName);
                        workUnit.setColumnName(baseColumn.getName());
                        workUnit.setTypeName(baseColumn.getTypeName());
                        workUnit.setSize(baseColumn.getSize());
                        workUnit.setDecimalSize(baseColumn.getDecimalSize());
                        alterTableChangeUnits.add(workUnit);         
                    }                        
                }
                else {
                    //new column
                    workUnit = new DBChangeUnit(DBChangeType.addColumn);
                    workUnit.setTableName(auditTableName);
                    workUnit.setColumnName(baseColumn.getName());
                    workUnit.setTypeName(baseColumn.getTypeName());
                    workUnit.setSize(baseColumn.getSize());
                    workUnit.setDecimalSize(baseColumn.getDecimalSize());
                    alterTableChangeUnits.add(workUnit);
                }
            }
            
            //end of table
            alterTableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
            
            //add the workUnits to the return value
            if (!renameColumnChangeUnits.isEmpty()){
                tableChangeUnits.addAll(renameColumnChangeUnits);
            }
            
            if (alterTableChangeUnits.size() > 3 ){
                tableChangeUnits.addAll(alterTableChangeUnits);
            }
            
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
        
        //sessionuser
        if (!sessionUserSQL.isEmpty()) {
            workUnit = new DBChangeUnit(DBChangeType.addTriggerSessionUser);
            workUnit.setColumnName(sessionUserColumn);
            workUnit.setTableName(baseTableName);
            workUnit.setAuditTableName(auditTableName);
            tableChangeUnits.add(workUnit);
        }
        
        
        //end trigger changes
        tableChangeUnits.add(new DBChangeUnit(DBChangeType.end));
        
        return tableChangeUnits;
        
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

    /**
     * Verify that the data types defined either by attribute or default
     * values are known to the DataSourceDMR dmr.  If the data types are not
     * set by attribute in the ConfigSource then the default java.sql.Types are
     * used, and the string values for the type names that correspond to those
     * default types are set in the respective fields.
     * 
     * @param dmr
     * @return True if all of the data types are found.  False if any of
     * them are not.
     */
    boolean verifyAuditColumnDataTypes(DataSourceDMR dmr) {

        boolean result = true;
        DataTypeDef dataTypeDef;
        
        //id column
        if (!auditIdTypeName.isEmpty()){
            dataTypeDef = dmr.getDataType(auditIdTypeName);
            if (dataTypeDef == null){
                logger.error("Configuration error.  Data type [{}] for audit id column is not valid", auditIdTypeName);
                result = false;
            }
        }
        else {
            dataTypeDef = dmr.getDataType(auditIdDefaultDataType);
            if (dataTypeDef == null){
                logger.error("Configuration error.  default data type [{}] for audit id column is not valid", auditIdDefaultDataType);
                result = false;
            }
            else {
                auditIdTypeName = dataTypeDef.type_name;
            }
        }
        
        //user column
        if (!auditUserTypeName.isEmpty()){
            dataTypeDef = dmr.getDataType(auditUserTypeName);
            if (dataTypeDef == null){
                logger.error("Configuration error.  Data type [{}] for audit user column is not valid", auditUserTypeName);
                result = false;
            }
        }
        else {
            dataTypeDef = dmr.getDataType(auditUserDefaultDataType);
            if (dataTypeDef == null){
                logger.error("Configuration error.  default data type [{}] for audit user column is not valid", auditUserDefaultDataType);
                result = false;
            }
            else {
                auditUserTypeName = dataTypeDef.type_name;
            }
        }
        
        //action column
        if (!auditActionTypeName.isEmpty()){
            dataTypeDef = dmr.getDataType(auditActionTypeName);
            if (dataTypeDef == null){
                logger.error("Configuration error.  Data type [{}] for audit action column is not valid", auditActionTypeName);
                result = false;
            }
        }
        else {
            dataTypeDef = dmr.getDataType(auditActionDefaultDataType);
            if (dataTypeDef == null){
                logger.error("Configuration error.  default data type [{}] for audit action column is not valid", auditActionDefaultDataType);
                result = false;
            }
            else {
                auditActionTypeName = dataTypeDef.type_name;
            }
        }
        
        //timestamp column
        if (!auditTimeStampTypeName.isEmpty()){
            dataTypeDef = dmr.getDataType(auditTimeStampTypeName);
            if (dataTypeDef == null){
                logger.error("Configuration error.  Data type [{}] for audit timestamp column is not valid", auditTimeStampTypeName);
                result = false;
            }
        }
        else {
            dataTypeDef = dmr.getDataType(auditTimeStampDefaultDataType);
            if (dataTypeDef == null){
                logger.error("Configuration error.  default data type [{}] for audit timestamp column is not valid", auditTimeStampDefaultDataType);
                result = false;
            }
            else {
                auditTimeStampTypeName = dataTypeDef.type_name;
            }
        }
        
        //session user column
        if (!sessionUserSQL.isEmpty()) {
            if (!sessionUserTypeName.isEmpty()) {
                dataTypeDef = dmr.getDataType(sessionUserTypeName);
                if (dataTypeDef == null) {
                    logger.error("Configuration error.  Data type [{}] for session user column is not valid", sessionUserTypeName);
                    result = false;
                }
            } else {
                dataTypeDef = dmr.getDataType(sessionUserDefaultDataType);
                if (dataTypeDef == null) {
                    logger.error("Configuration error.  Default data type [{}] for session user column is not valid", sessionUserDefaultDataType);
                    result = false;
                } else {
                    sessionUserTypeName = dataTypeDef.type_name;
                }
            }
        }
        return result;
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