/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigSourceTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigSourceTest.class);
    IdentifierMetaData idMetaData = mock(IdentifierMetaData.class);
    ConfigSource configSource;
    
    //Map<String, TableConfig> tablesConfig = (Map<String, TableConfig>) mock(Map.class);
    //Map<String, TableConfig> existingAuditTables = (Map<String, TableConfig>) mock(Map.class);

    
    public ConfigSourceTest() {    
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        configSource = new ConfigSource(idMetaData);
        //configSource.tablesConfig = (Map<String, TableConfig>) mock(Map.class);
        //configSource.existingAuditTables = (Map<String, TableConfig>) mock(Map.class);
        idMetaData.setStoresUpperCaseIds(true);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addExistingAuditTable method, of class ConfigSource.
     */
    @Test
    public void testAddExistingAuditTable() {
        System.out.println("addExistingAuditTable");
        String auditTableName = "zz_myTable";       
        when (idMetaData.convertId("zz_myTable")).thenReturn("ZZ_MYTABLE");
        configSource.addExistingAuditTable(auditTableName);
        assertEquals("ZZ_MYTABLE",idMetaData.convertId(auditTableName));
        boolean result = configSource.hasExistingAuditTable("zz_myTable");
        assertTrue(result);
    }

    /**
     * Test of hasExistingAuditTable method, of class ConfigSource.
     */
    @Test
    public void testHasExistingAuditTable() {
        System.out.println("hasExistingAuditTable");
        String auditTableName = "";
        ConfigSource instance = null;
        Boolean expResult = null;
        Boolean result = instance.hasExistingAuditTable(auditTableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addTableConfig method, of class ConfigSource.
     */
    @Test
    public void testAddTableConfig() {
        System.out.println("addTableConfig");
        String tableName = "";
        ConfigSource instance = null;
        instance.addTableConfig(tableName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ensureTableConfig method, of class ConfigSource.
     */
    @Test
    public void testEnsureTableConfig() {
        System.out.println("ensureTableConfig");
        String tableName = "";
        ConfigSource instance = null;
        instance.ensureTableConfig(tableName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addExcludedColumn method, of class ConfigSource.
     */
    @Test
    public void testAddExcludedColumn() {
        System.out.println("addExcludedColumn");
        String tableName = "";
        String columnName = "";
        ConfigSource instance = null;
        instance.addExcludedColumn(tableName, columnName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addIncludedColumn method, of class ConfigSource.
     */
    @Test
    public void testAddIncludedColumn() {
        System.out.println("addIncludedColumn");
        String tableName = "";
        String columnName = "";
        ConfigSource instance = null;
        instance.addIncludedColumn(tableName, columnName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTableConfig method, of class ConfigSource.
     */
    @Test
    public void testGetTableConfig() {
        System.out.println("getTableConfig");
        String tableName = "";
        ConfigSource instance = null;
        TableConfig expResult = null;
        TableConfig result = instance.getTableConfig(tableName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTablePostfix method, of class ConfigSource.
     */
    @Test
    public void testGetTablePostfix() {
        System.out.println("getTablePostfix");
        ConfigSource instance = null;
        String expResult = "";
        String result = instance.getTablePostfix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTablePostfix method, of class ConfigSource.
     */
    @Test
    public void testSetTablePostfix() {
        System.out.println("setTablePostfix");
        String tablePostfix = "";
        ConfigSource instance = null;
        instance.setTablePostfix(tablePostfix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTablePrefix method, of class ConfigSource.
     */
    @Test
    public void testGetTablePrefix() {
        System.out.println("getTablePrefix");
        ConfigSource instance = null;
        String expResult = "";
        String result = instance.getTablePrefix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTablePrefix method, of class ConfigSource.
     */
    @Test
    public void testSetTablePrefix() {
        System.out.println("setTablePrefix");
        String tablePrefix = "";
        ConfigSource instance = null;
        instance.setTablePrefix(tablePrefix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnPostfix method, of class ConfigSource.
     */
    @Test
    public void testGetColumnPostfix() {
        System.out.println("getColumnPostfix");
        ConfigSource instance = null;
        String expResult = "";
        String result = instance.getColumnPostfix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setColumnPostfix method, of class ConfigSource.
     */
    @Test
    public void testSetColumnPostfix() {
        System.out.println("setColumnPostfix");
        String columnPostfix = "";
        ConfigSource instance = null;
        instance.setColumnPostfix(columnPostfix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnPrefix method, of class ConfigSource.
     */
    @Test
    public void testGetColumnPrefix() {
        System.out.println("getColumnPrefix");
        ConfigSource instance = null;
        String expResult = "";
        String result = instance.getColumnPrefix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setColumnPrefix method, of class ConfigSource.
     */
    @Test
    public void testSetColumnPrefix() {
        System.out.println("setColumnPrefix");
        String columnPrefix = "";
        ConfigSource instance = null;
        instance.setColumnPrefix(columnPrefix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
