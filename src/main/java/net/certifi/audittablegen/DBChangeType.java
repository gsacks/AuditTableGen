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
    addColumn, //use on both table and trigger generation
    renameColumn,
    alterColumnType,
    alterColumnSize,
    alterColumnName,
    dropTriggers,
    createTriggers,
    fireOnInsert,
    fireOnUpdate,
    fireOnDelete,
    noFireOnColumn,
    notSet;  
}
