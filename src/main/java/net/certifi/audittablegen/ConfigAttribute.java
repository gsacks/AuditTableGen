/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigAttribute {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigAttribute.class);
    ConfigAttributeTypes type = ConfigAttributeTypes.unknown;
    String attribute = "";
    String tableName = "";
    String columnName = "";
    String value = "";

    public ConfigAttributeTypes getType() {
        return type;
    }

    public void setType(ConfigAttributeTypes type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = (attribute == null ? "" : attribute);
        
        try {
            type = ConfigAttributeTypes.valueOf(this.attribute.toLowerCase());
        } catch (EnumConstantNotPresentException e) {
            logger.error("Unknown attribute type: %s", this.attribute.toLowerCase());
            type = ConfigAttributeTypes.unknown;
        }
        
    }
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = (columnName == null ? "" : columnName);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = (tableName == null ? "" : tableName);
    }

    public String getValue() {
        return value;
    }

    public Boolean getBooleanValue(){
        if (value.equalsIgnoreCase("false")){
            return Boolean.FALSE;
        }
        else{
            return Boolean.TRUE;
        }
    }
    
    public void setValue(String value) {
        this.value = (value == null ? "" : value);
    }
    
}
