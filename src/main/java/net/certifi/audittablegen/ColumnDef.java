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

import java.util.Map;

/**
 *
 * @author Glenn Sacks
 */
public class ColumnDef {
    
    String name;
    String typeName; //database specific type name
    int sqlType; //java.sql.Types dataType
    int size; //field size 
    int decimalSize; //field precision
    DataTypeDef dataTypeDef;
    
    //the map contains all of the meta data.  It does not need to 
    //be set.  But if a DMR requires more information it can store
    //he full meta list here, and it will get it back in the changeSource
    //object.  This is just a fallback for an oddball case;
    Map<String, String> sourceMeta;

    public int getDecimalSize() {
        return decimalSize;
    }

    public void setDecimalSize(int decimalSize) {
        this.decimalSize = decimalSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Map<String, String> getSourceMeta() {
        return sourceMeta;
    }

    public void setSourceMeta(Map<String, String> sourceMeta) {
        this.sourceMeta = sourceMeta;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String type) {
        this.typeName = type;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public DataTypeDef getDataTypeDef() {
        return dataTypeDef;
    }

    public void setDataTypeDef(DataTypeDef dataTypeDef) {
        this.dataTypeDef = dataTypeDef;
    }

}
