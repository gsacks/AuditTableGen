/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.*;
import static org.junit.Assert.*;
import static org.fest.assertions.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Glenn Sacks
 */
public class HsqldbDMRTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HsqldbDMRTest.class);
    
    HsqldbDMR hsqldbDMR;
    //BasicDataSource dataSource = mock(BasicDataSource.class);
    BasicDataSource dataSource = new BasicDataSource();
    
    public HsqldbDMRTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:aname");
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        try {
            hsqldbDMR = new HsqldbDMR(dataSource);
        } catch (SQLException ex) {
            Logger.getLogger(HsqldbDMRTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of CreateTestTable method, of class HsqldbDMR.
     */
//    @Test
    public void testCreateTestTable() {
        System.out.println("CreateTestTable");
        //doThrow(new RuntimeException()).when(dataSource).getConnection();
        HsqldbDMR instance = hsqldbDMR ;
        try {
            instance.CreateTestTable();
        } catch (SQLException ex) {
            fail("CreateTestTable threw exception");
        }
    }

    /**
     * Test of SelectTestRow method, of class HsqldbDMR.
     */
//    @Test
    public void testSelectTestRow() {
        System.out.println("SelectTestRow");
        HsqldbDMR instance = hsqldbDMR;
        instance.SelectTestRow();

    }

    /**
     * Test of printDataSourceStats method, of class HsqldbDMR.
     */
//    @Test
    public void testPrintDataSourceStats() {
        System.out.println("printDataSourceStats");
        //DataSource ds = null;
        hsqldbDMR.printDataSourceStats();

    }

    /**
     * Test of shutdownDataSource method, of class HsqldbDMR.
     */
//    @Test
    
       /**
     * Test methods in order, of class HsqldbDMR.
     */
    @Test
    public void testAllMethodsInOrder() throws Exception {
        testCreateTestTable();
        testSelectTestRow();
        testPrintDataSourceStats();
    }

    @Test
    public void testGetRunTimeDataSource_0args() throws SQLException {
        logger.error("****************************************************");
        DataSource ds = HsqldbDMR.GetRunTimeDataSource();
        assertThat(ds.getConnection().getSchema()).isEqualTo("PUBLIC");
        logger.trace("Got connection to schema: {}", ds.getConnection().getSchema());
    }

    @Test
    public void testGetRunTimeDataSource_Properties() {
    }

    @Test
    public void testEnsureConnection() {
    }
}
