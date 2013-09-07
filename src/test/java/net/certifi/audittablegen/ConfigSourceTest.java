/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.Map;
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
public class ConfigSourceTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigSourceTest.class);
    IdentifierMetaData idMetaData = mock(IdentifierMetaData.class);
    ConfigSource configSource;
    
    Map<String, TableConfig> tablesConfig = (Map<String, TableConfig>) mock(Map.class);
    Map<String, TableConfig> existingAuditTables = (Map<String, TableConfig>) mock(Map.class);
    TableConfig tc = mock(TableConfig.class);

    
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
        configSource.tablesConfig = tablesConfig;
        configSource.existingAuditTables = existingAuditTables;
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
        String auditTableName2 = "zz_myOtherTable";
        
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        when (existingAuditTables.containsKey(auditTableName)).thenReturn(true);
        when (existingAuditTables.containsKey(auditTableName2)).thenReturn(false);
       configSource.addExistingAuditTable(auditTableName);
       configSource.addExistingAuditTable(auditTableName2);
        //boolean result1 = configSource.hasExistingAuditTable(auditTableName);
        //boolean result2 = configSource.hasExistingAuditTable(auditTableName2);
        verify (existingAuditTables).put(eq(auditTableName2), (TableConfig) anyObject());
        verify (existingAuditTables, times(0)).put(eq(auditTableName), (TableConfig) anyObject());

    }

    /**
     * Test of hasExistingAuditTable method, of class ConfigSource.
     */
    @Test
    public void testHasExistingAuditTable() {
        System.out.println("hasExistingAuditTable");
        String auditTableName = "zz_myTable";
        String auditTableName2 = "zz_myOtherTable";

        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        when (existingAuditTables.containsKey(auditTableName)).thenReturn(true);
        when (existingAuditTables.containsKey(auditTableName2)).thenReturn(false);
        boolean result1 = configSource.hasExistingAuditTable(auditTableName);
        boolean result2 = configSource.hasExistingAuditTable(auditTableName2);
        assertTrue(result1);
        assertFalse(result2);
    }

    /**
     * Test of addTableConfig method, of class ConfigSource.
     */
    @Test
    public void testAddTableConfig() {
        System.out.println("addTableConfig");
        String tableName = "myTable";
        String tableName2 = "myOtherTable";
        //when (idMetaData.convertId("myTable")).thenReturn("MYTABLE");
        when (tablesConfig.containsKey("myTable")).thenReturn(false);
        //when (idMetaData.convertId("myOtherTable")).thenReturn("MYOTHERTABLE");
        
        configSource.addTableConfig(tableName);
        verify (tablesConfig).put(eq("myTable"), (TableConfig) anyObject());
        
        //when (idMetaData.convertId("myOtherTable")).thenReturn("MYOTHERTABLE");
        when (tablesConfig.containsKey("myOtherTable")).thenReturn(true);
        configSource.addTableConfig(tableName2);
        verify (tablesConfig, times(0)).put(eq("myOtherTable"), (TableConfig) anyObject());
    }

    /**
     * Test of ensureTableConfig method, of class ConfigSource.
     */
    @Test
    public void testEnsureTableConfig() {
        
        System.out.println("ensureTableConfig");
        String tableName = "myTable";
        String tableName2 = "myOtherTable";
        //when (idMetaData.convertId("myTable")).thenReturn("MYTABLE");
        when (tablesConfig.containsKey("myTable")).thenReturn(false);
        //when (idMetaData.convertId("myOtherTable")).thenReturn("MYOTHERTABLE");
        
        configSource.ensureTableConfig(tableName);
        verify (tablesConfig).put(eq("myTable"), (TableConfig) anyObject());
        
        //when (idMetaData.convertId("myOtherTable")).thenReturn("MYOTHERTABLE");
        when (tablesConfig.containsKey("myOtherTable")).thenReturn(true);
        configSource.ensureTableConfig(tableName2);
        verify (tablesConfig, times(0)).put(eq("myOtherTable"), (TableConfig) anyObject());

    }

    /**
     * Test of addExcludedColumn method, of class ConfigSource.
     */
    @Test
    public void testAddExcludedColumn() {
        System.out.println("addExcludedColumn");
        String tableName = "mytable";
        String tableNotHere = "myothertable";
        String columnName = "mycolumn";
        
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        //test 1 - already exists in map
        when (tablesConfig.containsKey(tableName)).thenReturn(true);
        when (tablesConfig.get(tableName)).thenReturn(mock(TableConfig.class));
        configSource.addExcludedColumn(tableName, columnName);
        verify (tablesConfig, times(1)).get(eq(tableName));
        
        //test 2 - does not exist in map
        when (tablesConfig.containsKey(tableNotHere)).thenReturn(false);
        configSource.addExcludedColumn(tableNotHere, columnName);
        verify (tablesConfig, times(1)).put(eq(tableNotHere), (TableConfig) anyObject());
        
    }

    /**
     * Test of addIncludedColumn method, of class ConfigSource.
     */
    @Test
    public void testAddIncludedColumn() {
        System.out.println("addIncludedColumn");
        String tableName = "mytable";
        String tableNotHere = "myothertable";
        String columnName = "mycolumn";
        
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        //test 1 - already exists in map
        when (tablesConfig.containsKey(tableName)).thenReturn(true);
        when (tablesConfig.get(tableName)).thenReturn(mock(TableConfig.class));
        configSource.addIncludedColumn(tableName, columnName);
        verify (tablesConfig, times(1)).get(eq(tableName));
        
        //test 2 - does not exist in map
        when (tablesConfig.containsKey(tableNotHere)).thenReturn(false);
        configSource.addIncludedColumn(tableNotHere, columnName);
        verify (tablesConfig, times(1)).put(eq(tableNotHere), (TableConfig) anyObject());
    }

    /**
     * Test of getTableConfig method, of class ConfigSource.
     */
    @Test
    public void testGetTableConfig() {
        System.out.println("getTableConfig");
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        String tableName = "mytable";
        when (tablesConfig.get(tableName)).thenReturn(mock(TableConfig.class));
        TableConfig result = configSource.getTableConfig(tableName);
        assertNotNull(result);
    }

    /**
     * Test of getTablePostfix method, of class ConfigSource.
     */
    @Test
    public void testGetTablePostfix() {
        System.out.println("getTablePostfix");
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        configSource.setTablePostfix("postfix");
        String expResult = "postfix";
        String result = configSource.getTablePostfix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTablePostfix method, of class ConfigSource.
     */
    @Test
    public void testSetTablePostfix() {
        System.out.println("setTablePostfix");
      
        //nothing to do here.  The test is in the getter
        assertTrue(true);
    }

    /**
     * Test of getTablePrefix method, of class ConfigSource.
     */
    @Test
    public void testGetTablePrefix() {
        System.out.println("getTablePrefix");
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        configSource.setTablePrefix("prefix");
        String expResult = "prefix";
        String result = configSource.getTablePrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTablePrefix method, of class ConfigSource.
     */
    @Test
    public void testSetTablePrefix() {
        System.out.println("setTablePrefix");
       
        //nothing to do here.  The test is in the getter
        assertTrue(true);
    }

    /**
     * Test of getColumnPostfix method, of class ConfigSource.
     */
    @Test
    public void testGetColumnPostfix() {
        System.out.println("getColumnPostfix");
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });        
        
        //expected result is empty string because column postfix is disabled
        configSource.setColumnPostfix("postfix");
        String expResult = "";
        String result = configSource.getColumnPostfix();
        assertEquals(expResult, result);       
    }

    /**
     * Test of setColumnPostfix method, of class ConfigSource.
     */
    @Test
    public void testSetColumnPostfix() {
        System.out.println("setColumnPostfix");
        
        //nothing to do here.  The test is in the getter
        assertTrue(true);
    }

    /**
     * Test of getColumnPrefix method, of class ConfigSource.
     */
    @Test
    public void testGetColumnPrefix() {
        System.out.println("getColumnPrefix");
        when(idMetaData.convertId(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        //expected result is empty string because column prefix is disabled
        configSource.setColumnPrefix("prefix");
        String expResult = "";
        String result = configSource.getColumnPrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of setColumnPrefix method, of class ConfigSource.
     */
    @Test
    public void testSetColumnPrefix() {
        System.out.println("setColumnPrefix");
       
        //nothing to do here.  The test is in the getter
        assertTrue(true);
    }
}
