/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author Glenn Sacks
 */
public class DBChangeUnit {

    DBChangeType changeType = DBChangeType.notSet;
    String tableName = "";
    String auditTableName = "";
    String columnName = "";
    String newColumnName = "";
    Boolean firesTrigger = Boolean.TRUE;
    String dataType = "";
    int size = 0;
    int decimalSize = 0;
    Boolean identity = Boolean.FALSE;

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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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
    
    
}
