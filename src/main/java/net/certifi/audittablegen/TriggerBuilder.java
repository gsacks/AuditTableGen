/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author jputney
 */
public class TriggerBuilder {

    TriggerConfiguration triggerConfiguration;
    DbSchemaRepository dbSchemaRepository;

    public void buildTrigger(String tableName) {
        if (! triggerConfiguration.skipTable(tableName)) {
            dbSchemaRepository.doMagicTriggerCreation(tableName);
        }
    }
}
