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
    String auditTableName;
    Boolean excluded;
    ArrayList<ColumnChangeSource> columns;
    
    TableChangeSource (Boolean isNewTable, String auditTableName, Boolean excluded){
        this.isNewTable = isNewTable;
        this.auditTableName = auditTableName;
        this.excluded = excluded;
        columns = new ArrayList<>();
    }
    
    void addColumn (ColumnChangeSource ccs){
        columns.add(ccs);
    }
    
}
