/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;


/**
 *
 * @author Glenn Sacks
 */
public interface DataSourceDMR {
    
    Boolean hasConfigSource ();
    void setSchema(String schema);
    String getSchema();
    void setAuditConfigTable(String table);
    String getAuditConfigTable();
    
}
