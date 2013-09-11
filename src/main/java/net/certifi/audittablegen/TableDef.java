/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Glenn Sacks
 */
public class TableDef {
    
    String name;
    List<ColumnDef> columns;
    
    TableDef (){
        columns = new ArrayList<>();
    }
    
    /**
     * Add column to the existing column list
     * @param cd 
     */
    void addColumn (ColumnDef cd){
        columns.add(cd);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnDef> getColumns() {
        return columns;
    }

    /**
     * Set the list of columns. Replaces the
     * existing list
     * 
     * @param columns 
     */
    public void setColumns(List<ColumnDef> columns) {
        this.columns = columns;
    }
    
}
