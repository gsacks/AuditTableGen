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
public class GenericDMRIT {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenericDMRIT.class);
    //GenericDMR dmr = mock(GenericDMR.class);
    GenericDMR dmr;
    JDBCDataSource dataSource;
    DatabaseMetaData dmd;
    ConfigSource configSource;
    IdentifierMetaData idMetaData;
    
    public GenericDMRIT() {
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
     * Test of hasAuditConfigTable method, of class GenericDMR.
     */
    @Test
    public void testHasConfigSource() {
        System.out.println("loadConfigSource");

        //test the default values (should pass)
        Boolean result = dmr.hasAuditConfigTable();
        assertTrue(result);
        
        //test another value (should fail)
        dmr.unverifiedAuditConfigTable = "not_here";
        dmr.verifiedAuditConfigTable = null;
        result = dmr.hasAuditConfigTable();
        assertFalse(result);
    }
    
    @Test
    public void testGetColumnMetaDataForTable() {
        
        Map columnMetaDataForTable = dmr.getColumnMetaDataForTable("AUDITCONFIG");
        for (Iterator it = columnMetaDataForTable.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> metaDataEntry = (Map.Entry<String, String>) it.next();
            System.out.printf("Metadata column: %s, Metadata values: %s", metaDataEntry.getKey(), metaDataEntry.getValue());
            System.out.println();
        }
        
        logger.debug("");
    }

    /**
     * Test of loadConfigAttributes method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigAttributes() {
        System.out.println("loadConfigAttributes");
        
        ConfigSource configSource = mock(ConfigSource.class);
        
        dmr.loadConfigAttributes(configSource);
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

    /**
     * Test of setSchema method, of class GenericDMR.
     */
    @Test
    public void testSetSchema() {
        System.out.println("setSchema");
        String unverifiedSchema = "Public";
        dmr.verifiedSchema = null;
        dmr.setSchema(unverifiedSchema);

        assertEquals(dmr.unverifiedSchema, unverifiedSchema);
        assertNotNull(dmr.verifiedSchema);
    }

    /**
     * Test of getSchema method, of class GenericDMR.
     */
    @Test
    public void testGetSchema() {
        System.out.println("getSchema");
        dmr.unverifiedSchema = "pUbLiC";
        String expResult = "PUBLIC";
        String result = dmr.getSchema();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAuditConfigTableName method, of class GenericDMR.
     */
    @Test
    public void testSetAuditConfigTable() {
        System.out.println("setAuditConfigTable");

        String unverifiedAuditConfigTable = "AuDitCONfig";
        dmr.verifiedAuditConfigTable = null;
        dmr.setAuditConfigTableName(unverifiedAuditConfigTable);

        assertEquals(dmr.unverifiedAuditConfigTable, unverifiedAuditConfigTable);
        assertNotNull(dmr.verifiedAuditConfigTable);       
    }

    /**
     * Test of getAuditConfigTableName method, of class GenericDMR.
     */
    @Test
    public void testGetAuditConfigTable() {
        System.out.println("getAuditConfigTable");
        dmr.unverifiedAuditConfigTable = "auditCONFIG";
        String expResult = "AUDITCONFIG";
        String result = dmr.getAuditConfigTableName();
        assertEquals(expResult, result);
    }
    
    
}
