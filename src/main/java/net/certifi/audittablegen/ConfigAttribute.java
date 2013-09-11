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
    String attribute = null;
    String tableName = null;
    String columnName = null;
    String value = null;

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
        this.attribute = attribute;
        
        try {
            type = ConfigAttributeTypes.valueOf(attribute.toLowerCase());
        } catch (EnumConstantNotPresentException e) {
            logger.error("Unknown attribute type: %s", attribute.toLowerCase());
            type = ConfigAttributeTypes.unknown;
        }
        
    }
    
    public String getColumn() {
        return columnName;
    }

    public void setColumn(String column) {
        this.columnName = column;
    }

    public String getTable() {
        return tableName;
    }

    public void setTable(String table) {
        this.tableName = table;
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
        this.value = value;
    }
    
}
