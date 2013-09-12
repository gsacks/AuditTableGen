/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author Glenn Sacks
 */
public enum DBChangeType {
    begin,
    end,
    createTable,
    alterTable,
    addColumn,
    alterColumnType,
    alterColumnSize,
    alterColumnName,
    dropTriggers,
    createTriggers,
    fireOnInsert,
    fireOnUpdate,
    fireOnDelete,
    addTriggerColumn,
    notSet;  
}
