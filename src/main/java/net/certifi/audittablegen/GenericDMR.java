/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;


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
    
        /**
     * Generate a DataSource from Properties 
     * @param props
     * @return BasicDataSource as DataSource
     */
    static DataSource GetRunTimeDataSource(Properties props){
        
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName(props.getProperty("driver", ""));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        //dataSource.setValidationQuery("SELECT 1");
        
        return dataSource;
    }
    
    public boolean EnsureConnection (){
        
        return true;
    }
    
}
