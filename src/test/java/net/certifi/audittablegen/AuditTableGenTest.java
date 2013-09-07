/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class AuditTableGenTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditTableGenTest.class);
    AuditTableGen atg;
    DataSource dataSource = mock(DataSource.class);
    Connection conn = mock(Connection.class);
    DataSourceDMR dmr = mock(DataSourceDMR.class);
    DatabaseMetaData dmd = mock(DatabaseMetaData.class);
    Properties clientProps = mock(Properties.class);
    
    
    public AuditTableGenTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws SQLException {
        atg = new AuditTableGen(dataSource, "public");
        atg.dataSource = dataSource;
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialize method, of class AuditTableGen.
     */
    @Test
    public void testInitialize() throws Exception {

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.getClientInfo()).thenReturn(clientProps);
        when(conn.getMetaData()).thenReturn(dmd);
        when(dmd.getDriverName()).thenReturn("mock-mock-mock");
        
        atg.initialize();
        //called once in method and once in GenericDMR constructor
        verify(dataSource, times(2)).getConnection();
        //code was removed, should not be called
        verify(conn, times(0)).getClientInfo();
        //called once in method and once in GenericDMR constructor
        verify(conn, times(2)).getMetaData();
        
    }

    /**
     * Test of getDataSourceInfo method, of class AuditTableGen.
     */
    @Test
    public void testGetDataSourceInfo() throws Exception {
        System.out.println("getDataSourceInfo");
        atg.dmr = dmr;
        ResultSet rs = mock(ResultSet.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.getMetaData()).thenReturn(dmd);
        when(dmd.getCatalogs()).thenReturn(rs);
        when(dmd.getSchemas()).thenReturn(rs2);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("TABLE_CAT")).thenReturn("fakeCatalog");
        when(rs2.next()).thenReturn(true, false);
        when(rs2.getString("TABLE_SCHEM")).thenReturn("fakeSchema");
        when(rs2.getString("TABLE_CATALOG")).thenReturn("fakeCatalogAgain");
        when(dmr.hasConfigSource()).thenReturn(true);
        String result = atg.getDataSourceInfo();
        verify(rs, times(2)).next();
        verify(rs2, times(2)).next();
        verify(rs, times(1)).close();
        verify(rs2, times(1)).close();
        verify(conn, times(1)).close();
        assertTrue(result.contains("fakeCatalog"));
        assertTrue(result.contains("fakeSchema"));
        assertTrue(result.contains("fakeCatalogAgain"));
    }

    /**
     * Test of main method, of class AuditTableGen.
     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        AuditTableGen.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of usage method, of class AuditTableGen.
     */
//    @Test
//    public void testUsage() {
//        System.out.println("usage");
//        // TODO review the generated test code and remove the default call to fail.
//        // fail("The test case is a prototype.");
//    }

    /**
     * Test of getRunTimeProperties method, of class AuditTableGen.
     */
    @Test
    public void testGetRunTimeProperties() {
        System.out.println("getRunTimeProperties");
        CommandLine cmd = mock(CommandLine.class);
        
        when(cmd.hasOption("url")).thenReturn(false);
        when(cmd.getOptionValue("driver", "")).thenReturn("");
        when(cmd.hasOption("filename")).thenReturn(false);
        Properties result = AuditTableGen.getRunTimeProperties(cmd);
        assertNotNull(result);
        verify(cmd, times(1)).hasOption("url");
        verify(cmd, times(1)).getOptionValue("driver","");
        verify(cmd, times(1)).hasOption("filename");
    }

    /**
     * Test of getRunTimeDataSource method, of class AuditTableGen.
     */
    @Test
    public void testGetRunTimeDataSource() {
        System.out.println("getRunTimeDataSource");
        
        Properties props = mock(Properties.class);
        when(props.containsKey("url")).thenReturn(false);
        
        //default behavior is connect to in memory db (for testing).
        DataSource result = AuditTableGen.getRunTimeDataSource(props);
        assertNotNull(result);
    }

    /**
     * Test of updateAuditTables method, of class AuditTableGen.
     */
    @Test
    public void testUpdateAuditTables() {
        System.out.println("updateAuditTables");
        
        DataSourceDMR dmr = mock(DataSourceDMR.class);
        atg.dmr = dmr;
        atg.initialized = true;
        String testConfigSQL = "create auditConfig and stuff";
        when(dmr.hasConfigSource()).thenReturn(false);
        when(dmr.getCreateConfigSQL()).thenReturn(testConfigSQL);
        when(dmr.validateCreateConfig()).thenReturn(true);
        Boolean result = atg.updateAuditTables();
        verify(dmr, times(1)).executeCreateConfigSQL();
        assertEquals(true, result);
                
    }
    
        /**
     * Test of updateAuditTables method, of class AuditTableGen.
     */
    @Test
    public void test2UpdateAuditTables() {
        System.out.println("updateAuditTables");
        
        DataSourceDMR dmr = mock(DataSourceDMR.class);
        atg.dmr = dmr;
        atg.initialized = true;
        String testUpdateSQL = "create a bunch of zz_ table";
        when(dmr.hasConfigSource()).thenReturn(true);
        when(dmr.getUpdateSQL()).thenReturn(testUpdateSQL);
        when(dmr.validateUpdate()).thenReturn(true);
        Boolean result = atg.updateAuditTables();
        verify(dmr, times(1)).executeUpdateSQL();
        assertEquals(true, result);
                
    }

}
