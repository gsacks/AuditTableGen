/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author jputney
 */
interface DbSchemaRepository {

    void doMagicTriggerCreation(String tableName);
}
