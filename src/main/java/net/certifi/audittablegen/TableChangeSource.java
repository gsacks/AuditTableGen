/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;

/**
 *
 * @author Glenn Sacks
 */
public class TableChangeSource {
    
    Boolean isNewTable;
    String tableName;
    ArrayList<ColumnChangeSource> columns;
    
    TableChangeSource (Boolean isNewTable, String tableName){
        this.isNewTable = isNewTable;
        this.tableName = tableName;
        columns = new ArrayList<>();
    }
    
    void addColumn (ColumnChangeSource ccs){
        columns.add(ccs);
    }
    
}
