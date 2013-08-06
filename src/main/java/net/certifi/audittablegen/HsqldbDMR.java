/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
//import org.hsqldb.Statement;
import org.hsqldb.jdbc.JDBCDataSource;

/**
 *
 * @author Glenn Sacks
 */
public class HsqldbDMR implements DataSourceDMR {
    
    BasicDataSource dataSource;
    private Boolean _keepOpen;
    private Connection _conn;

    public Boolean getKeepOpen() {
        return _keepOpen;
    }

    /**
     * For this basic non-pooled DataSource, define whether or not
     * a single connection is held open for the duration of the object
     * or if it opens a new connection for each method call.
     * 
     * @param _keepOpen 
     */
    public void setKeepOpen(Boolean _keepOpen) {
        this._keepOpen = _keepOpen;
    }
    
    private Connection GetConnection(){
     
        if (_keepOpen){
            
        }
        else {
            
        }
        
        return _conn;
        
    }
    HsqldbDMR (){
        
        dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:aname");
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        
    }
    
            
            
    void CreateTestTable (){
        
        System.out.println("dataSourse is NOT null:" + dataSource.getUrl());
        
        String SQL = "Create table test1 (test1Id integer not null identity, test1Data integer  )";
        try {
            Connection conn = dataSource.getConnection();
            System.out.println("here1");
            if (dataSource == null){
                System.out.println("dataSourse is null");
                System.out.println("here2");
            }
            else {
                System.out.println("dataSourse is NOT null:" + dataSource.toString());
                System.out.println("here3");
            }
            
            Statement stmt = conn.createStatement();
            
            if (stmt == null){
                System.out.println("stmt is null");
            }
            else {
                System.out.println("stmt is NOT null");
            }
            
            
            stmt.executeUpdate(SQL);
            
            stmt.executeUpdate("insert into test1 (test1Data) values 1");
            
            stmt.close();
            
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(HsqldbDMR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    void SelectTestRow (){
        
        try {
            Connection conn = dataSource.getConnection();
            
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("select * from test1");

            while (rs.next()) {
                String id = rs.getString("test1Id");
                String data = rs.getString("test1Data");
                System.out.println("Id:" + id + "  Data:" + data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HsqldbDMR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    public void printDataSourceStats() {
        System.out.println("NumActive: " + dataSource.getNumActive());
        System.out.println("NumIdle: " + dataSource.getNumIdle());
    }

    public void shutdownDataSource() throws SQLException {
        //BasicDataSource bds = (BasicDataSource) ds;
        dataSource.close();
       
    }

    
    public void SetKeepOpen (Boolean value){
        
        _keepOpen = value;
                    
                    
        
    }
    /**
     * When KeepOpen is true, ensures that the connection is working.
     * When KeepOpen is false, attempts a temporary connection to 
     *   validate the DataSource, but does not keep it open.
     * 
     * @return true if a connection to the source can be established
     *         false if a connection cannot be established
     */
    public boolean EnsureConnection() {
        
        Connection conn;
        
        try {
            if (_keepOpen){
                if (_conn == null ){
                    _conn = dataSource.getConnection();
                }
                if ( _conn != null && _conn.isValid(15)){
                    return true;
                }
            }
            else {
                conn = dataSource.getConnection();
                if ( conn != null && conn.isValid(15)){
                    conn.close();
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(HsqldbDMR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
   
}
