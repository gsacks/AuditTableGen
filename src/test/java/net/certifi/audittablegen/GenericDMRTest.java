/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.datatype.LiquibaseDataType;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
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
    ConfigSource configSource;
    IdentifierMetaData idMetaData;
    
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

            dmr = new GenericDMR(dataSource, "PUBLIC"); 

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            Liquibase liquibase = new Liquibase("src/test/resources/changesets/changeset-init-config.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(null);
            
            liquibase = new Liquibase("src/test/resources/changesets/changeset-sample-tables.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(null);
            
            Connection conn = dataSource.getConnection();
            dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, "AUDITCONFIG", null);
            while (rs.next()){
                if (rs.getString("TABLE_NAME").equalsIgnoreCase("auditconfig")){
                    logger.info ("Validating test setup - Audit Configuration created");
                }
            }
            
        } catch (SQLException e){
            logger.error("error setting up unit tests: ", e);
        } catch (LiquibaseException le){
            logger.error("liquibase error", le);
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
        String schema = "PUBLIC";
        GenericDMR instance = dmr;
        instance.setSchemaName(schema);
        instance.getConnection();
        assertEquals(instance.targetSchema, schema);       
    }

    /**
     * Test of getRunTimeDataSource method, of class GenericDMR.
     */
    @Test
    public void testGetRunTimeDataSource() {
        System.out.println("getRunTimeDataSource");
        Properties props = mock(Properties.class);
        DataSource result = GenericDMR.getRunTimeDataSource(props);
        assertNotNull(result);
    }

    /**
     * Test of hasConfigSource method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigSource() {
        System.out.println("loadConfigSource");

        Boolean expResult = true;
        Boolean result = dmr.hasConfigSource();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetColumnMetaDataForTable() {
        
        Map columnMetaDataForTable = dmr.getColumnMetaDataForTable("AUDITTABLECONFIG");
        for (Iterator it = columnMetaDataForTable.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> metaDataEntry = (Map.Entry<String, String>) it.next();
            System.out.printf("Metadata column: %s, Metadata values: %s", metaDataEntry.getKey(), metaDataEntry.getValue());
            System.out.println();
        }
        
        logger.debug("");
    }

    /**
     * Test of hasConfigSource method, of class GenericDMR.
     */
    @Test
    public void testHasConfigSource() {
        System.out.println("hasConfigSource");
        GenericDMR instance = null;
        Boolean expResult = null;
        Boolean result = instance.hasConfigSource();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getConnection method, of class GenericDMR.
     */
    @Test
    public void testGetConnection() {
        System.out.println("getConnection");
        GenericDMR instance = null;
        Connection expResult = null;
        Connection result = instance.getConnection();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMetaData method, of class GenericDMR.
     */
    @Test
    public void testGetMetaData() {
        System.out.println("getMetaData");
        GenericDMR instance = null;
        DatabaseMetaData expResult = null;
        DatabaseMetaData result = instance.getMetaData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadConfigAttributes method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigAttributes() {
        System.out.println("loadConfigAttributes");
        ConfigSource configSource = null;
        GenericDMR instance = null;
        instance.loadConfigAttributes(configSource);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadConfigTables method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigTables() {
        System.out.println("loadConfigTables");
        ConfigSource configSource = null;
        GenericDMR instance = null;
        instance.loadConfigTables(configSource);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuditTableSql method, of class GenericDMR.
     */
    @Test
    public void testGetAuditTableSql() {
        System.out.println("getAuditTableSql");
        ConfigSource configSource = null;
        String tableName = "";
        GenericDMR instance = null;
        String expResult = "";
        String result = instance.getAuditTableSql(configSource, tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNewAuditTableColumnMetaData method, of class GenericDMR.
     */
    @Test
    public void testGetNewAuditTableColumnMetaData() {
        System.out.println("getNewAuditTableColumnMetaData");
        ConfigSource configSource = null;
        String tableToAudit = "";
        GenericDMR instance = null;
        Map expResult = null;
        Map result = instance.getNewAuditTableColumnMetaData(configSource, tableToAudit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuditTableCreateSql method, of class GenericDMR.
     */
    @Test
    public void testGetAuditTableCreateSql() {
        System.out.println("getAuditTableCreateSql");
        ConfigSource configSource = null;
        String tableName = "";
        GenericDMR instance = null;
        String expResult = "";
        String result = instance.getAuditTableCreateSql(configSource, tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuditTableModifySql method, of class GenericDMR.
     */
    @Test
    public void testGetAuditTableModifySql() {
        System.out.println("getAuditTableModifySql");
        ConfigSource configSource = null;
        String tableName = "";
        GenericDMR instance = null;
        String expResult = "";
        String result = instance.getAuditTableModifySql(configSource, tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
}
