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
public class ColumnChangeSource {
    
    String name;
    String type;
    String oldType;
    int size; //for postres char size and numeric size
    int oldSize;
    int decimalSize; //for numeric and decimal
    int oldDecimalSize;
    Boolean isNew;
    //should not need these, but providing in case some DMR needs more information
    Map<String, String> sourceMeta;
    Map<String, String> destMeta;
    
    ColumnChangeSource (String name, String type,
            int size, int decimalSize, Map sourceMeta){
        
        this.name = name;
        this.type = type;
        this.oldType = null;
        this.size = size;
        this.oldSize = 0;
        this.decimalSize = decimalSize;
        this.oldDecimalSize = 0;
        this.isNew = true;
        this.sourceMeta = sourceMeta;
        this.destMeta = null;
    }
    
    ColumnChangeSource (String name, String type, String oldType,
            int size, int oldSize, int decimalSize, int oldDecimalSize, 
            Map sourceMeta, Map destMeta){
        
        this.name = name;
        this.type = type;
        this.oldType = oldType;
        this.size = size;
        this.oldSize = oldSize;
        this.decimalSize = decimalSize;
        this.oldDecimalSize = oldDecimalSize;
        this.isNew = false;
        this.sourceMeta = sourceMeta;
        this.destMeta = destMeta;
        
    }
    
    Boolean isNew(){
        return isNew;
    }
    
    Boolean isSizeDecreased(){
        if (size < oldSize
                || decimalSize < oldDecimalSize ){
            return true;
        }
        else {
            return false;
        }
    }
    
    Boolean isTypeChanged(){
        if (isNew
                || type.equals(oldType)){
            return false;
        }
        else {
            return true;
        }
    }
}
