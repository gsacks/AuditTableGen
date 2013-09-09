/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author Glenn Sacks
 */
public class TriggerColumnChangeSource {
    
    String baseColumnName;
    String auditColumnName;
    Boolean firesTrigger;
    
    TriggerColumnChangeSource(String baseColumnName, String auditColumnName, Boolean firesTrigger){
        this.baseColumnName = baseColumnName;
        this.auditColumnName = auditColumnName;
        this.firesTrigger = firesTrigger;
    }

    public String getAuditColumnName() {
        return auditColumnName;
    }

    public String getBaseColumnName() {
        return baseColumnName;
    }

    public Boolean getFiresTrigger() {
        return firesTrigger;
    }
    
    
}
