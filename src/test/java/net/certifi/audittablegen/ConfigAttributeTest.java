/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigAttributeTest {
    
    public ConfigAttributeTest() {
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
     * Test of getType method, of class ConfigAttribute.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        ConfigAttribute instance = new ConfigAttribute();
        instance.type = ConfigAttributeTypes.auditupdate;
        ConfigAttributeTypes expResult = ConfigAttributeTypes.auditupdate;
        ConfigAttributeTypes result = instance.getType();
        assertEquals(expResult, result);

    }

    /**
     * Test of setType method, of class ConfigAttribute.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        ConfigAttributeTypes type = ConfigAttributeTypes.tableprefix;
        ConfigAttribute instance = new ConfigAttribute();
        instance.setType(type);
        assertEquals(type, instance.type);
    }

    /**
     * Test of getAttribute method, of class ConfigAttribute.
     */
    @Test
    public void testGetAttribute() {
        System.out.println("getAttribute");
        ConfigAttribute instance = new ConfigAttribute();
        String expResult = "pink";
        instance.attribute = "pink";
        String result = instance.getAttribute();
        assertEquals(expResult, result);

    }

    /**
     * Test of setAttribute method, of class ConfigAttribute.
     */
    @Test
    public void testSetAttribute() {
        System.out.println("setAttribute");
        String attribute = "include";
        ConfigAttribute instance = new ConfigAttribute();
        instance.setAttribute(attribute);
        instance.setAttribute(attribute);

        assertEquals(ConfigAttributeTypes.include, instance.type );
    }

    /**
     * Test of getColumnName method, of class ConfigAttribute.
     */
    @Test
    public void testGetColumnName() {
        System.out.println("getColumnName");
        ConfigAttribute instance = new ConfigAttribute();
        instance.columnName = "lname";
        String expResult = "lname";
        String result = instance.getColumnName();
        assertEquals(expResult, result);

    }

    /**
     * Test of setColumnName method, of class ConfigAttribute.
     */
    @Test
    public void testSetColumnName() {
        System.out.println("setColumnName");
        String columnName = "fname";
        ConfigAttribute instance = new ConfigAttribute();
        instance.setColumnName(columnName);
        assertEquals(columnName, instance.columnName);

    }

    /**
     * Test of getTableName method, of class ConfigAttribute.
     */
    @Test
    public void testGetTableName() {
        System.out.println("getTableName");
        ConfigAttribute instance = new ConfigAttribute();
        String expResult = "PERSON";
        instance.tableName = "PERSON";
        String result = instance.getTableName();
        assertEquals(expResult, result);

    }

    /**
     * Test of setTableName method, of class ConfigAttribute.
     */
    @Test
    public void testSetTableName() {
        System.out.println("setTableName");
        String tableName = "Contract";
        ConfigAttribute instance = new ConfigAttribute();
        instance.setTableName(tableName);

        assertEquals(tableName, instance.tableName);
    }

    /**
     * Test of getValue method, of class ConfigAttribute.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        ConfigAttribute instance = new ConfigAttribute();
        String expResult = "JohnDoe";
        instance.value = "johndoe";
        String result = instance.getValue();
        assertNotEquals(expResult, result);

    }

    /**
     * Test of getBooleanValue method, of class ConfigAttribute.
     */
    @Test
    public void testGetBooleanValue() {
        System.out.println("getBooleanValue");
        ConfigAttribute instance = new ConfigAttribute();
        Boolean expResult = true;
        instance.value = "notABoolean";
        Boolean result = instance.getBooleanValue();
        assertEquals(expResult, result);

    }

    /**
     * Test of setValue method, of class ConfigAttribute.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        String value = "hiJeff";
        ConfigAttribute instance = new ConfigAttribute();
        instance.setValue(value);
        assertEquals(value, instance.value);
        
    }

    /**
     * Test of setBooleanValue method, of class ConfigAttribute.
     */
    @Test
    public void testSetBooleanValue() {
        System.out.println("setBooleanValue");
        Boolean value = Boolean.FALSE;
        ConfigAttribute instance = new ConfigAttribute();
        instance.setBooleanValue(value);
        assertEquals("false", instance.value);
        
    }
}
