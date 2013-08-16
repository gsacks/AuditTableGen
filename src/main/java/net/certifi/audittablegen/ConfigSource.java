/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigSource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigSource.class);
    
    Map<String, Map> tablesAttributes;
    String prefix;
    String postfix;
    
    ConfigSource(){
        tablesAttributes = new HashMap<String, Map>();        
    }
    
    boolean addExcludeTable (String tableName){
        
        if(tablesAttributes.containsKey(tableName)){
            //table already exists
            return false;
        }
        else {
            Map m = new HashMap<String, String>();
            tablesAttributes.put(tableName, m);
            return true;
        }
    }
    
    void addExcludeColumn (String tableName, String columnName){
    
        Map<String, String> table;
        
        if(tablesAttributes.containsKey(tableName)){
            table = tablesAttributes.get("tableName");
        }
        else {
            table = new HashMap<String, String>();
            tablesAttributes.put(tableName, table);
        }
        
        table.put("excludeColumn", columnName);
        
    }
}
