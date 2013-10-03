/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class DBChangeUnit {

    private static final Logger logger = LoggerFactory.getLogger(DBChangeUnit.class);
    DBChangeType changeType = DBChangeType.notSet;
    String tableName = "";
    String auditTableName = "";
    String columnName = "";
    String newColumnName = "";
    String foreignTable = "";
    Boolean firesTrigger = Boolean.TRUE;
    String typeName = "";
    int size = 0;
    int decimalSize = 0;
    Boolean identity = Boolean.FALSE;
    //DataTypeDef dataTypeDef = null;

    public DBChangeUnit( DBChangeType changeType) {
        this.changeType = changeType;
    }
    
    public DBChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(DBChangeType changeType) {
        this.changeType = changeType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getNewColumnName() {
        return newColumnName;
    }

    public void setNewColumnName(String newColumnName) {
        this.newColumnName = newColumnName;
    }

    public String getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String dataType) {
        this.typeName = dataType;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    public void setDecimalSize(int decimalSize) {
        this.decimalSize = decimalSize;
    }

    public Boolean getFiresTrigger() {
        return firesTrigger;
    }

    public void setFiresTrigger(Boolean firesTrigger) {
        this.firesTrigger = firesTrigger;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAuditTableName() {
        return auditTableName;
    }

    public void setAuditTableName(String auditTableName) {
        this.auditTableName = auditTableName;
    }

    public Boolean getIdentity() {
        return identity;
    }

    public void setIdentity(Boolean identity) {
        this.identity = identity;
    }

//    public DataTypeDef getDataTypeDef() {
//        return dataTypeDef;
//    }
//
//    public void setDataTypeDef(DataTypeDef dataTypeDef) {
//        this.dataTypeDef = dataTypeDef;
//    }
    
    @Override
    public String toString(){
    
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(changeType.name()).append(" ");
        
        switch (changeType){
            case begin:
            case end:
                break;
            case createTable:
            case alterTable:
                builder.append("table=").append(this.getTableName()).append(" ");
                break;
            case addColumn:
            case alterColumnSize:
            case alterColumnType:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("column=").append(this.getColumnName()).append(" ");
                builder.append("datatype=").append(this.getTypeName()).append(" ");
                builder.append("size=").append(this.getSize()).append(" ");
                builder.append("decimalsize=").append(this.getDecimalSize()).append(" ");
                builder.append("identity=").append(this.getIdentity().toString()).append(" ");
                builder.append("foreignTable=").append(this.getForeignTable()).append(" ");
                break;
            case alterColumnName:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("column=").append(this.getColumnName()).append(" ");
                builder.append("newname=").append(this.getNewColumnName()).append(" ");
                builder.append("datatype=").append(this.getTypeName()).append(" ");
                builder.append("size=").append(this.getSize()).append(" ");
                builder.append("decimalsize=").append(this.getDecimalSize()).append(" ");
                builder.append("identity=").append(this.getIdentity().toString()).append(" ");
                break;
            case createTriggers:
            case dropTriggers:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("audittable=").append(this.getAuditTableName()).append(" ");
                break;
            case addTriggerColumn:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("audittable=").append(this.getAuditTableName()).append(" ");
                builder.append("column=").append(this.getColumnName()).append(" ");
                builder.append("fires=").append(this.getFiresTrigger().toString()).append(" ");
                break;
            case fireOnDelete:
            case fireOnInsert:
            case fireOnUpdate:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("audittable=").append(this.getAuditTableName()).append(" ");
                builder.append("fires=").append(this.getFiresTrigger()).append(" ");
                break;
            case addTriggerAction:
            case addTriggerTimeStamp:
            case addTriggerUser:
                builder.append("table=").append(this.getTableName()).append(" ");
                builder.append("audittable=").append(this.getAuditTableName()).append(" ");
                builder.append("column=").append(this.getColumnName()).append(" ");
                break;
            case notSet:
            default:
                break;
        }
        
        builder.append("]");
        
        return builder.toString();
        
    }
    
    static String ListToString (List<DBChangeUnit> units){
        
        StringBuilder builder = new StringBuilder();
        
         for ( DBChangeUnit unit : units) {
             builder.append(unit.toString()).append(System.lineSeparator());
         }
         
         return builder.toString();
    }
    
    static Boolean validateUnit (DBChangeUnit unit){
                
        return Boolean.TRUE;
    }
    
    static Boolean validateUnit (DBChangeUnit unit, DBChangeUnit parentUnit){

        //this should validate that the unit has all of the proper fields
        //populated for its type.  But since the value of data_type is DB
        //dependent, and the value is size is dependent on the data type,
        //there isn't an straight forward way to do this.  So for now, just
        //return true.
        
        return Boolean.TRUE;
    }
    
    static Boolean validateUnitList (List<DBChangeUnit> units){
        
        //List<DBChangeUnit> workList = null;
        
        Boolean beginTag = false;
        DBChangeType workListTag = DBChangeType.notSet;
        DBChangeUnit parentUnit =  null;
        int i = 0;
        Boolean valid = true;
        
        //pull apart each <begin-stuff-end> and submit into queue.
        for ( DBChangeUnit unit : units) {
            i++;
            if (!valid){
                logger.info(ListToString(units));
                break;
            }
            switch (unit.getChangeType()){
                case begin:
                    if (beginTag){
                        logger.info("improperly formed List<DBChangeType>.  Missing [end] before element %d", i);
                        valid = false;
                    }
                    else {
                        //start 'work unit list'
                        beginTag = true;
                    }
                    break;
                case end:
                    //submit 'work unit list'
                    if (!beginTag){
                        logger.info ("improperly formed List<DBChangeType>.  Missing [end] without [begin] at element %d", i);
                        valid = false;
                    }
                    else if ( workListTag == DBChangeType.notSet){
                        logger.info ("improperly formed List<DBChangeType>.  [end] without declaring change type at element %d", i);
                        valid = false;
                    }
                    else {
                        //properly formed change unit list if we got here.  Reset tags in case multiple sets in one list
                        beginTag = false;
                        workListTag =  DBChangeType.notSet;
                        parentUnit = null;
                    }
                    break;
                case createTable:
                case alterTable:
                case createTriggers:
                case dropTriggers:
                    if (!beginTag){
                        //begin tag missing.
                        //could be implied, but treat as error condition.
                        logger.info ("improperly formed List<DBChangeType>.  Missing [begin] before element %d", i);
                        valid = false;
                    }
                    else if (workListTag != DBChangeType.notSet){
                        logger.info ("improperly formed List<DBChangeType>.  Missing [end] before element %d", i);
                        valid = false;
                    }
                    else {
                        workListTag = unit.getChangeType();
                        parentUnit = unit;
                        valid = validateUnit(unit);
                    }
                    break;
                case addColumn:
                    if (!beginTag){
                        logger.info ("improperly formed List<DBChangeType>.  Missing [begin] before element %d", i);
                        valid = false;
                    }
                    else if (workListTag != DBChangeType.createTable
                            && workListTag != DBChangeType.alterTable) {
                        logger.info ("improperly formed List<DBChangeType>. Unit{%s} not of valid for {%s} at element %d",
                                unit.getChangeType().toString(), workListTag.toString(), i);
                        valid = false;
                    }
                    else {
                        valid = validateUnit(unit, parentUnit);
                    }

                    break;
                case alterColumnName:
                case alterColumnSize:
                case alterColumnType:
                    if (!beginTag){
                        logger.info ("improperly formed List<DBChangeType>.  Missing [begin] before element %d", i);
                    }
                    if ( workListTag != DBChangeType.alterTable) {
                        logger.info ("improperly formed List<DBChangeType>. Unit{%s} not of valid for {%s} at element %d",
                                unit.getChangeType().toString(), workListTag.toString(), i);
                    }
                    else {
                        valid = validateUnit(unit, parentUnit);
                    }
                    break;
                case addTriggerColumn:
                case fireOnInsert:
                case fireOnUpdate:
                case fireOnDelete:
                case addTriggerAction:
                case addTriggerTimeStamp:
                case addTriggerUser:
                    if (beginTag == false){
                        logger.info ("improperly formed List<DBChangeType>.  Missing [begin] before element %d", i);
                    }
                    if (workListTag != DBChangeType.createTriggers){
                        throw new RuntimeException ("improperly formed List<DBChangeType>. Unit{" +
                                unit.getChangeType().toString() + "} not of valid for {"
                                + workListTag.toString() +"}");
                    }
                    else {
                        valid = validateUnit(unit, parentUnit);
                    }
                    break;
                case notSet:
                default:
                    logger.info ("improperly formed List<DBChangeType>.  unkown type{%s} at element %d", unit.getChangeType().toString(), i);
                    valid = false;
            }
        }
        
        return valid;
    }
}
