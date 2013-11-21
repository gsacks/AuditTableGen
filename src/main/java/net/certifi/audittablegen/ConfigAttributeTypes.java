/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

/**
 *
 * @author Glenn Sacks
 */
public enum ConfigAttributeTypes {
    
    exclude,
    include,
    tableprefix,
    tablepostfix,
    columnprefix,
    columnpostfix,
    auditinsert,
    auditupdate,
    auditdelete,
    iddatatype,
    userdatatype,
    actiondatatype,
    timestampdatatype,
    sessionusersql,
    sessionuserdatatype,
    sessionuserdatasize,
    unknown;
    
}
