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
public class TriggerChangeSource {
    
    Boolean hasInsertTrigger = true;
    Boolean hasUpdateTrigger = true;
    Boolean hasDeleteTrigger = true;
    String baseTableName;
    String auditTableName;
    ArrayList<TriggerColumnChangeSource> columns;

    TriggerChangeSource (String baseTableName, String auditTableName) {
        this.baseTableName = baseTableName;
        this.auditTableName = auditTableName;
    }
    
    void addColumn(TriggerColumnChangeSource tccs){
        columns.add(tccs);
    }
    
}

