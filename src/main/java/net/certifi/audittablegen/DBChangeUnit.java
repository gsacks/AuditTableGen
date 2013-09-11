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
    String columnName = "";
    Boolean firesTrigger = Boolean.TRUE;
    String dataType = "";
    int size = 0;
    int decimalSize = 0;

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
    
    
}
