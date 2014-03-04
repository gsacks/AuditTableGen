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

    public void setValue(String value) {
        this.value = (value == null ? "" : value);
    }

    public Boolean getBooleanValue(){
        if (value.equalsIgnoreCase("false")){
            return Boolean.FALSE;
        }
        else{
            return Boolean.TRUE;
        }
    }
    
    public void setBooleanValue(Boolean value){
        if (value.equals(Boolean.FALSE)){
            this.value = "false";
        }
        else {
            this.value = "true";
        }
    }
    
    public int getIntValue() {

        Integer i;

        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            i = 0;
            logger.error("Invalid integer value for attribute {}", attribute);
            throw e;
        }
        return i;
    }
    
    public void setIntValue (Integer value){
        
        this.value = value.toString();
                
    }

    
}
