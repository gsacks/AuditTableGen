/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;


/**
 *
 * @author Glenn Sacks
 */
class GenericDMR implements DataSourceDMR {
    
    DataSource dataSource;
    DatabaseMetaData dmd;
    
    GenericDMR (DataSource ds) throws SQLException{
        
        dataSource = ds;
        Connection conn = ds.getConnection();
        dmd = conn.getMetaData();
        conn.close();
        
    }
    
    public boolean EnsureConnection (){
        
        return true;
    }
    
}
