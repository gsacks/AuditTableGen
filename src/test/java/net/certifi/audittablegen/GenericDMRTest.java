/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class GenericDMRTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenericDMRTest.class);
    //GenericDMR dmr = mock(GenericDMR.class);
    GenericDMR dmr;
    JDBCDataSource dataSource;
    DatabaseMetaData dmd;
    
    public GenericDMRTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
        dataSource = new JDBCDataSource();
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:aname");
        
        try {

            dmr = new GenericDMR(dataSource);           
            Connection conn = dataSource.getConnection();
            dmd = conn.getMetaData();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("create table auditconfig (attribute varchar(100), target varchar(100) )");
            logger.info("ran create table auditconfig");
            
            ResultSet rs = dmd.getTables(null, null, "AUDITCONFIG", null);
            while (rs.next()){
                if (rs.getString("TABLE_NAME").equalsIgnoreCase("auditconfig")){
                    logger.info ("Test setup - Audit Configuration created");
                }
            }
        } catch (SQLException e){
            logger.error("error setting up unit tests: " + e.getMessage());
        }
        
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setSchemaName method, of class GenericDMR.
     */
    @Test
    public void testSetSchemaName() {
        System.out.println("setSchemaName");
        String schema = "";
        GenericDMR instance = null;
        instance.setSchemaName(schema);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRunTimeDataSource method, of class GenericDMR.
     */
    @Test
    public void testGetRunTimeDataSource() {
        System.out.println("getRunTimeDataSource");
        Properties props = null;
        DataSource expResult = null;
        DataSource result = GenericDMR.getRunTimeDataSource(props);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ensureConnection method, of class GenericDMR.
     */
    @Test
    public void testEnsureConnection() {
        System.out.println("ensureConnection");
        GenericDMR instance = null;
        boolean expResult = false;
        boolean result = instance.ensureConnection();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadConfigSource method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigSource() {
        System.out.println("loadConfigSource");
        GenericDMR instance = dmr;
        dmr.dataSource = dataSource;
        dmr.dmd = dmd;
        Boolean expResult = true;
        Boolean result = instance.loadConfigSource();
        assertEquals(expResult, result);

    }
}
