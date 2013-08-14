/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Glenn Sacks
 */
public class TestConnection {
    static void getData (Connection conn) throws SQLException{
        
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("Select * from testSandbox.table1");
        
        while (rs.next()){
            String data = rs.getString("data");
            System.out.println(data+ "\n");          
        }
        
    }
}
