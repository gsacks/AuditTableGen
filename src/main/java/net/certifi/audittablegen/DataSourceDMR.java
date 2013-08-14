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
    
    boolean ensureConnection ();
    Boolean loadConfigSource ();
    
}
