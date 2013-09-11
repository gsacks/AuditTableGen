/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.*;
import java.util.Map.Entry;
import java.util.*;
import javax.sql.DataSource;
import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
    DataSource dataSource = mock(DataSource.class);
    DatabaseMetaData dmd = mock(DatabaseMetaData.class);
    ConfigSource configSource = mock(ConfigSource.class);
    IdentifierMetaData idMetaData = mock(IdentifierMetaData.class);
    Connection conn = mock(Connection.class);
    
    public GenericDMRTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws SQLException {
        
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.getMetaData()).thenReturn(dmd);
        when(dmd.storesLowerCaseIdentifiers()).thenReturn(true);
        when(dmd.storesMixedCaseIdentifiers()).thenReturn(false);
        when(dmd.storesUpperCaseIdentifiers()).thenReturn(false);
        dmr = new GenericDMR(dataSource);
        //dmr.unverifiedSchema = "public";
        //dmr.configSource = configSource;
        
//        dataSource = new JDBCDataSource();
//        dataSource.setPassword("");
//        dataSource.setUrl("jdbc:hsqldb:mem:aname");
//        
//        try {
//
//            dmr = new GenericDMR(dataSource, "PUBLIC"); 
//
//            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
//            Liquibase liquibase = new Liquibase("src/test/resources/changesets/changeset-init-config.xml", new FileSystemResourceAccessor(), database);
//            liquibase.update(null);
//            
//            liquibase = new Liquibase("src/test/resources/changesets/changeset-sample-tables.xml", new FileSystemResourceAccessor(), database);
//            liquibase.update(null);
//            
//            Connection conn = dataSource.getConnection();
//            dmd = conn.getMetaData();
//            ResultSet rs = dmd.getTables(null, null, "AUDITCONFIG", null);
//            while (rs.next()){
//                if (rs.getString("TABLE_NAME").equalsIgnoreCase("auditconfig")){
//                    logger.info ("Validating test setup - Audit Configuration created");
//                }
//            }
//            
//        } catch (SQLException e){
//            logger.error("error setting up unit tests: ", e);
//        } catch (LiquibaseException le){
//            logger.error("liquibase error", le);
//        }
        
        
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
    public void testHasConfigSource() throws SQLException {
        System.out.println("loadConfigSource");

        ResultSet rs = mock(ResultSet.class);
        //test the default values (should pass)
        dmr.verifiedAuditConfigTable = "AUDITCONFIG";
        Boolean result = dmr.hasAuditConfigTable();
        assertTrue(result);
        
        //test another value (should fail)
        //verified schema (the second var below should be null in this test
        when(dmd.getTables(null, null, null, null)).thenReturn(rs);
        dmr.unverifiedAuditConfigTable = "not_here";
        dmr.verifiedAuditConfigTable = null;
        dmr.verifiedSchema = null;
        Boolean result2 = dmr.hasAuditConfigTable();
        verify (dmd, times(1)).getTables(null, null, null, null);
        assertNull(dmr.verifiedAuditConfigTable);
        assertFalse(result2);
    }
    
    @Test
    public void testGetColumnMetaDataForTable() throws SQLException {
        
        String tableName = "myTable";
        String verifiedSchema = "public";
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData rsmd = mock (ResultSetMetaData.class);
        dmr.verifiedSchema = verifiedSchema;
        
        when(dmd.getColumns(null, verifiedSchema, tableName, null)).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(rsmd);
        when(rsmd.getColumnCount()).thenReturn(2);
        when(rs.isBeforeFirst()).thenReturn(true);        
        when(rs.next()).thenReturn(true, true, false);
        when(rsmd.getColumnName(anyInt())).thenReturn("metaTestCol1", "metaTestCol2");
        when(rs.getString(anyInt())).thenReturn("value1", "value2");
        when(rs.getString("COLUMN_NAME")).thenReturn("myTableId", "myTableData");
        
        Map mapResult = dmr.getColumnMetaDataForTable(tableName);
        
        verify (rs, times(2)).getString(1);
        verify (rs, times(2)).getString(2);
        verify (rs, times(2)).getString("COLUMN_NAME");
        verify (rs, times(3)).next();
        verify (rsmd, times(1)).getColumnCount();
        verify (rsmd, times(2)).getColumnName(1);
        verify (rsmd, times(2)).getColumnName(2);
        verifyNoMoreInteractions(rsmd);
                
        assertTrue(mapResult.containsKey("myTableId"));
        assertTrue(mapResult.containsKey("myTableData"));

    }

    /**
     * Test of loadConfigAttributes method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigAttributes() throws SQLException {
        System.out.println("loadConfigAttributes");
        
        dmr.verifiedAuditConfigTable = "auditconfig";
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(stmt);
        //when (stmt.executeQuery("select attribute, table, column, value from auditconfig")).thenReturn(rs);
        when (stmt.executeQuery(anyString())).thenReturn(rs);
        when (rs.next()).thenReturn(true, true, true, false);
        when(rs.getString("attribute")).thenReturn("exclude", "include", "tableprefix");
        when(rs.getString("table")).thenReturn("table1","table2");
        when(rs.getString("column")).thenReturn("column1","column2");
        when(rs.getString("value")).thenReturn("ZZ_");
        
        dmr.loadConfigAttributes(configSource);
        verify (configSource, times(1)).addExcludedColumn("table1", "column1");
        verify (configSource, times(1)).addIncludedColumn("table2", "column2");
        verify (configSource, times(1)).setTablePrefix("ZZ_");
      
    }

    /**
     * Test of loadConfigTables method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigTables() throws SQLException {
        System.out.println("loadConfigTables");
        
        dmr.verifiedSchema = "public";
        Iterator i1 = mock (Iterator.class);
        Iterator i2 = mock (Iterator.class);       
        //Iterator i2 = mock (Iterator.class);
        Entry<String, TableConfig> entry = mock(Entry.class);
        Entry<String, TableConfig> entry2 = mock(Entry.class);
        Set<Entry<String, TableConfig>> set = (Set<Entry<String, TableConfig>>) mock (Set.class);
        Set<Entry<String, TableConfig>> set2 = (Set<Entry<String, TableConfig>>) mock (Set.class);
        Map<String, TableConfig> tablesConfig = (Map<String, TableConfig>) mock(Map.class);
        Map<String, TableConfig> auditTablesConfig = (Map<String, TableConfig>) mock(Map.class);
        Map<String, String> columns = mock(Map.class);
        TableConfig tc = mock(TableConfig.class);
        GenericDMR spyDMR = spy(dmr);
        
        configSource.existingTables = tablesConfig;
        configSource.existingAuditTables = auditTablesConfig;
        
        ResultSet rs = mock(ResultSet.class);
        when(dmd.getTables(null, dmr.verifiedSchema, null, new String[]{"TABLE"})).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, true, true, false);
        when(rs.getString("TABLE_NAME")).thenReturn("address","person","phone", "zz_address");
        when(configSource.getTablePrefix()).thenReturn("zz_");
        when(configSource.getTablePostfix()).thenReturn("");
        
        //I need to use a spy b/c of the self-referential method call. not sure if this 
        //  is something that inidicates a redesign is in order or not.
        
        //mock iteration ove tables and call to getColumnMetaDataForTable...
        when(tablesConfig.entrySet()).thenReturn(set);
        when(set.iterator()).thenReturn(i1);
        when(i1.hasNext()).thenReturn(true, true, true, false);
        when(i1.next()).thenReturn(entry);
        when(entry.getKey()).thenReturn("address","person","phone");
        when(entry.getValue()).thenReturn(tc);
        
        //mock iteration over audito tables and call to getColumnMetaDataForTable...
        when(auditTablesConfig.entrySet()).thenReturn(set2);
        when(set2.iterator()).thenReturn(i2);
        when(i2.hasNext()).thenReturn(true, false);
        when(i2.next()).thenReturn(entry2);
        when(entry2.getKey()).thenReturn("zz_address");
        when(entry2.getValue()).thenReturn(tc);
        
        //return same mock object on every call to getColumnMetaDataForTable...
        doReturn(columns).when(spyDMR).getColumnMetaDataForTable(anyString());
        
        spyDMR.loadConfigTables(configSource);
        verify(configSource, times(3)).ensureTableConfig(anyString());
        verify(configSource, times(1)).addExistingAuditTable("zz_address");
        verify(entry, times(3)).getValue();
        verify(entry2, times(1)).getValue();
        

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
        
        TableConfig tc = mock(TableConfig.class);
        String tableToAudit = "phone";
        
        Map<String, Map<String, String>> columns = (Map <String, Map<String, String>>) mock(Map.class);
        Entry<String, Map<String, String>> entryOuter = mock(Entry.class);
        Set<Entry<String, Map<String, String>>> setOuter = (Set<Entry<String, Map<String, String>>>) mock (Set.class);
        Iterator iOuter = mock (Iterator.class);
        
        Map<String, String> columnsMeta = (Map <String, String>) mock(Map.class);
        Entry<String, String> entryInner = mock(Entry.class);
        Set<Entry<String, String>> setInner = (Set<Entry<String, String>>) mock (Set.class);
        Iterator iInner = mock (Iterator.class);
        
        Map excludedColumns = mock(Map.class);
        Map includedColumns = mock(Map.class);
        tc.excludedColumns = excludedColumns;
        tc.includedColumns = includedColumns;
        when (excludedColumns.containsKey(anyString())).thenReturn(false);
        when (includedColumns.containsKey(anyString())).thenReturn(false);
        when (configSource.getColumnPrefix()).thenReturn("");
        when (configSource.getColumnPostfix()).thenReturn("");
        
        when (configSource.getTableConfig(tableToAudit)).thenReturn(tc);
        when (tc.getColumns()).thenReturn(columns);
            when(columns.entrySet()).thenReturn(setOuter);
            when(setOuter.iterator()).thenReturn(iOuter);
            when(iOuter.hasNext()).thenReturn(true, true, true, false); //three columns
            when(iOuter.next()).thenReturn(entryOuter);
            when(entryOuter.getKey()).thenReturn("areacode","phone","ext");
            when(entryOuter.getValue()).thenReturn(columnsMeta);
                when(columnsMeta.entrySet()).thenReturn(setInner);
                when(setInner.iterator()).thenReturn(iInner);
                //there should be 3 iterations of the outer loop, return true, true, false for each iteration
                //this should be done with thenAnswer(), but brute force works too...
                when(iInner.hasNext()).thenReturn(true, true, false, true, true, false, true, true, false);
                when(iInner.next()).thenReturn(entryInner);
                when(entryInner.getKey()).thenReturn("metaCol1", "metaCol2", "metaCol1","metaCol2","metaCol1","metaCol2");
                when(entryInner.getValue()).thenReturn("value1-1","value1-2","value2-1","value2-2","value3-1","value3-2");
                
        
        Map resultMap = dmr.getNewAuditTableColumnMetaData(configSource, tableToAudit);
        
        assertNotNull(resultMap);
        verify(entryOuter, times(3)).getValue();
        verify(entryInner, times(6)).getValue();
        assertTrue(resultMap.containsKey("areacode"));
        assertTrue( ((String)((Map) resultMap.get("areacode")).get("metaCol1")).equals("value1-1"));
        assertTrue(resultMap.containsKey("phone"));
        assertTrue( ((String)((Map) resultMap.get("phone")).get("metaCol2")).equals("value2-2"));
        assertTrue(resultMap.containsKey("ext"));
        assertTrue( ((String)((Map) resultMap.get("ext")).get("metaCol2")).equals("value3-2"));
        
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
    public void testSetSchema() throws SQLException {
        System.out.println("setSchema");
        String unverifiedSchema = "Public";
        dmr.verifiedSchema = null;

        //these are in setup()
        //when(dataSource.getConnection()).thenReturn(conn);
        //when(conn.getMetaData()).thenReturn(dmd);
        
        ResultSet rs = mock(ResultSet.class);
        when (dmd.getSchemas()).thenReturn(rs);
        when (rs.next()).thenReturn(true, false, true, false);
        when (rs.getString("TABLE_SCHEM")).thenReturn("wrong_result","public");
        
        //1st pass
        dmr.setSchema(unverifiedSchema);
        assertEquals(dmr.unverifiedSchema, unverifiedSchema);
        assertNull(dmr.verifiedSchema);
        
        //2nd pass
        dmr.setSchema(unverifiedSchema);
        assertEquals(dmr.unverifiedSchema, unverifiedSchema);
        assertNotNull(dmr.verifiedSchema);
 
    }

    /**
     * Test of getSchema method, of class GenericDMR.
     */
    @Test
    public void testGetSchema() throws SQLException {
        System.out.println("getSchema");

        //1st pass
        dmr.verifiedSchema = "PUBLIC";
        String expResult = "PUBLIC";
        String result = dmr.getSchema();        
        assertEquals(expResult, result);
        
        //2nd pass - this will call setSchema to verify the unvirified value
        dmr.verifiedSchema = null;
        dmr.unverifiedSchema = "PubLic";
        ResultSet rs = mock(ResultSet.class);
        when (dmd.getSchemas()).thenReturn(rs);
        when (rs.next()).thenReturn(true, false);
        when (rs.getString("TABLE_SCHEM")).thenReturn("public");
        String result2 = dmr.getSchema();
        assertEquals("public", result2);
               
    }

    /**
     * Test of setAuditConfigTableName method, of class GenericDMR.
     */
    @Test
    public void testSetAuditConfigTable() throws SQLException {
        System.out.println("setAuditConfigTable");

        String unverifiedAuditConfigTable = "AuDitCONfig";
        dmr.verifiedSchema = "public";
        dmr.verifiedAuditConfigTable = null;

        
        ResultSet rs = mock(ResultSet.class);
        when (dmd.getTables(null, "public", null, null)).thenReturn(rs);
        when (rs.next()).thenReturn(true, false, true, false);
        when (rs.getString("TABLE_NAME")).thenReturn("wrong_result","auditconfig");

        //1st pass
        dmr.setAuditConfigTableName(unverifiedAuditConfigTable);
        assertEquals(dmr.unverifiedAuditConfigTable, unverifiedAuditConfigTable);
        assertNull(dmr.verifiedAuditConfigTable);       
        
        //2nd pass
        dmr.setAuditConfigTableName(unverifiedAuditConfigTable);
        assertEquals(dmr.unverifiedAuditConfigTable, unverifiedAuditConfigTable);
        assertNotNull(dmr.verifiedAuditConfigTable); 
    }

    /**
     * Test of getAuditConfigTableName method, of class GenericDMR.
     */
    @Test
    public void testGetAuditConfigTable() throws SQLException {
        System.out.println("getAuditConfigTable");
        
        dmr.verifiedSchema = "PUBLIC";
        
        //1st pass
        String expResult = "AUDITCONFIG";
        dmr.verifiedAuditConfigTable = "AUDITCONFIG";
        String result = dmr.getAuditConfigTableName();        
        assertEquals(expResult, result);
        
        //2nd pass - this will call setSchema to verify the unvirified value
        dmr.unverifiedAuditConfigTable = "auditCONFIG";
        dmr.verifiedAuditConfigTable = null;
        ResultSet rs = mock(ResultSet.class);
        when (dmd.getTables(null, "PUBLIC", null, null)).thenReturn(rs);
        when (rs.next()).thenReturn(true, false);
        when (rs.getString("TABLE_NAME")).thenReturn("auditconfig");
        String result2 = dmr.getAuditConfigTableName();
        assertEquals("auditconfig", result2);
    }
    
    
}
