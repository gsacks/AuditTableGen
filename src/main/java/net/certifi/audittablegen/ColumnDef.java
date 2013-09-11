/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.Map;

/**
 *
 * @author Glenn Sacks
 */
public class ColumnDef {
    
    String name;
    String type;
    int size; //for postres char size and numeric size
    int decimalSize; //for numeric and decimal
    
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
}
