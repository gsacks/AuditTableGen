/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.SQLException;
import java.util.List;
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
public class ChangeSourceFactoryTest {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ChangeSourceFactoryTest.class);
    ConfigSource configSource = new ConfigSource();
    ConfigSource configMock = mock(ConfigSource.class);
    
    public ChangeSourceFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isPatternMatch method, of class ChangeSourceFactory.
     */
    @Test
    public void testIsPatternMatch() {
        System.out.println("isPatternMatch");
        String str = "lastName";
        String pattern = "*";
        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        Boolean expResult = true;
        Boolean result = instance.isPatternMatch(str, pattern);
        assertEquals(expResult, result);

    }

    /**
     * Test of isTableExcluded method, of class ChangeSourceFactory.
     */
    @Test
    public void testIsTableExcluded() {
        System.out.println("isTableExcluded");
        String tableName = "transient";
        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        ConfigAttribute attrib = new ConfigAttribute();
        attrib.setType(ConfigAttributeTypes.exclude);
        attrib.setTableName("transient");
        configSource.addAttribute(attrib);
        Boolean expResult = true;
        
        Boolean result = instance.isTableExcluded(tableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of isColumnExcluded method, of class ChangeSourceFactory.
     */
    @Test
    public void testIsColumnExcluded() {
        System.out.println("isColumnExcluded");
        String tableName = "user";
        String columnName = "lastLogin";
        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        ConfigAttribute attrib = new ConfigAttribute();
        //exclude
        attrib.setType(ConfigAttributeTypes.exclude);
        attrib.setTableName("*");
        attrib.setColumnName("*");
        configSource.addAttribute(attrib);
        //include - should override wildcard
        ConfigAttribute attrib2 = new ConfigAttribute();
        attrib2.setType(ConfigAttributeTypes.include);
        attrib2.setTableName("user");
        attrib2.setColumnName("lastLogin");
        configSource.addAttribute(attrib2);
        Boolean expResult = false;
        Boolean result = instance.isColumnExcluded(tableName, columnName);
        assertEquals(expResult, result);

        
    }

    /**
     * Test of hasTriggerType method, of class ChangeSourceFactory.
     */
    @Test
    public void testHasTriggerType() {
        System.out.println("hasTriggerType");
        String tableName = "invoice";
        ConfigAttributeTypes type = ConfigAttributeTypes.auditinsert;
        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        ConfigAttribute attrib = new ConfigAttribute();
        //trigger attribute
        attrib.setType(ConfigAttributeTypes.auditdelete);
        attrib.setTableName("invoice");
        attrib.setBooleanValue(Boolean.FALSE);
        configSource.addAttribute(attrib);
        Boolean expResult = true;
        Boolean result = instance.hasTriggerType(tableName, type);
        assertEquals(expResult, result);

    }

    /**
     * Test of getDBChangeList method, of class ChangeSourceFactory.
     */
    @Test
    public void testGetDBChangeList_TableDef() {
        System.out.println("getDBChangeList");
        
        //set-up using real objects
        TableDef td = new TableDef();
        td.name = "Table1";
        ColumnDef cd = new ColumnDef();
        cd.name = "Table1Id";
        cd.typeName = "integer";
        td.addColumn(cd);
        cd = new ColumnDef();
        cd.name = "Data";
        cd.typeName = "varchar";
        cd.size = 255;
        td.addColumn(cd);
        configSource.addTable(td);

        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        
        List expResult = null;
        List<DBChangeUnit> result = instance.getDBChangeList(td);
        for ( DBChangeUnit unit : result){
            //these are the real test results...
            logger.info(unit.toString());
        }
        assertNotNull(result);

    }
   
    /**
     * Test of getDBChangeList method, of class ChangeSourceFactory.
     */
    @Test
    public void testGetDBChangeList_0args_testChanges() {
        System.out.println("getDBChangeList");

        //set-up using real objects
        TableDef td = new TableDef();
        td.name = "Table1";
        ColumnDef cd = new ColumnDef();
        cd.name = "Table1Id";
        cd.typeName = "integer";
        td.addColumn(cd);
        cd = new ColumnDef();
        cd.name = "Data";
        cd.typeName = "varchar";
        cd.size = 255;
        td.addColumn(cd);
        configSource.addTable(td);
        
        td = new TableDef();
        td.name = "zz_Table1";
        //zz_id
        cd = new ColumnDef();
        cd.name = "zz_Table1Id";
        cd.typeName = "integer";
        td.addColumn(cd);
        //zz_user
        cd = new ColumnDef();
        cd.name = "zz_userId";
        cd.typeName = "char";
        td.addColumn(cd);
        //zz_timestamp
        cd = new ColumnDef();
        cd.name = "zz_ts";
        cd.typeName = "timestamp";
        td.addColumn(cd);
        //zz_action
        cd = new ColumnDef();
        cd.name = "zz_action";
        cd.typeName = "char";
        td.addColumn(cd);
        
        cd = new ColumnDef();
        cd.name = "Table1Id";
        cd.typeName = "integer";
        td.addColumn(cd);
        cd = new ColumnDef();
        cd.name = "Data";
        cd.typeName = "char";
        cd.size = 255;
        td.addColumn(cd);
        configSource.addTable(td);
        

        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        List<DBChangeUnit> result = instance.getDBChangeList();
        logger.info ("\n{}",DBChangeUnit.ListToString(result));
        for ( DBChangeUnit unit : result){
            //these are the real test results...
            //logger.info(unit.toString());
        }
        assertNotNull(result);
    }


    /**
     * Test of getDBChangeList method, of class ChangeSourceFactory.
     */
    @Test
    public void testGetDBChangeList_String() {
        
        System.out.println("getDBChangeList");
       //set-up using real objects
        TableDef td = new TableDef();
        td.name = "Table1";
        ColumnDef cd = new ColumnDef();
        cd.name = "Table1Id";
        cd.typeName = "integer";
        td.addColumn(cd);
        cd = new ColumnDef();
        cd.name = "Data";
        cd.typeName = "varchar";
        cd.size = 255;
        td.addColumn(cd);
        configSource.addTable(td);

        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        List<DBChangeUnit> result = instance.getDBChangeList("Table1");
        for ( DBChangeUnit unit : result){
            //these are the real test results...
            logger.info(unit.toString());
        }
        assertNotNull(result);
        assertTrue (result.size() > 0 );
        //this is fragile.  result size might change...
        //assertEquals(14, result.size());
        
    }

    /**
     * Test of verifyAuditColumnDataTypes method, of class ChangeSourceFactory.
     */
    @Test
    public void testVerifyAuditColumnDataTypes() throws SQLException {
        System.out.println("verifyAuditColumnDataTypes");
        DataSourceDMR dmr = new HsqldbDMR(HsqldbDMR.getRunTimeDataSource());
        ChangeSourceFactory instance = new ChangeSourceFactory(configSource);
        boolean expResult = true;
        boolean result = instance.verifyAuditColumnDataTypes(dmr);
        assertEquals(expResult, result);

    }
}
