/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.postgresql.Driver;

/**
 *
 * @author Glenn Sacks
 */
public class PostgresqlDMR extends GenericDMR {
    

    PostgresqlDMR (DataSource ds) throws SQLException{
        
        super(ds);
        
    }
        
    /**
     * Generate a Postgresql DataSource from Properties 
     * @param props
     * @return BasicDataSource as DataSource
     */
    static DataSource GetRunTimeDataSource(Properties props){
        
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setValidationQuery("SELECT 1");
        
        return dataSource;
    }
            
    void CreateTestTable () throws SQLException{
        
        System.out.println("dataSourse is NOT null:" + dmd.getURL());
        
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
            Logger.getLogger(PostgresqlDMR.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PostgresqlDMR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    public void printDataSourceStats() {
        try {
            if (dataSource.isWrapperFor(BasicDataSource.class)){
                BasicDataSource bds = dataSource.unwrap(BasicDataSource.class);
                System.out.println("NumActive: " + bds.getNumActive());
                System.out.println("NumIdle: " + bds.getNumIdle());
                
            }
            else {
                System.out.println ("DataSource Stats not available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostgresqlDMR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
            conn = dataSource.getConnection();
            if (conn != null && conn.isValid(15)) {
                conn.close();
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PostgresqlDMR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
   
}