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
public class IdentifierMetaDataTest {
    
    public IdentifierMetaDataTest() {
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
     * Test of getStoresLowerCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testGetStoresLowerCaseIds() {
        System.out.println("getStoresLowerCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        Boolean expResult = true;
        Boolean result = instance.getStoresLowerCaseIds();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStoresLowerCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testSetStoresLowerCaseIds() {
        System.out.println("setStoresLowerCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        instance.setStoresLowerCaseIds(false);
        assertEquals(false, instance.getStoresLowerCaseIds());
        instance.setStoresLowerCaseIds(true);
        assertEquals(true, instance.getStoresLowerCaseIds());
        assertEquals(false, instance.getStoresUpperCaseIds());
        assertEquals(false, instance.getStoresMixedCaseIds());
    }

    /**
     * Test of getStoresMixedCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testGetStoresMixedCaseIds() {
        System.out.println("getStoresMixedCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        Boolean expResult = false;
        Boolean result = instance.getStoresMixedCaseIds();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStoresMixedCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testSetStoresMixedCaseIds() {
        System.out.println("setStoresMixedCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        instance.setStoresMixedCaseIds(true);
        assertEquals(true, instance.getStoresMixedCaseIds());
        assertEquals(false, instance.getStoresLowerCaseIds());
        assertEquals(false, instance.getStoresUpperCaseIds());
        instance.setStoresMixedCaseIds(false);
        assertEquals(false, instance.getStoresMixedCaseIds());
    }

    /**
     * Test of getStoresUpperCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testGetStoresUpperCaseIds() {
        System.out.println("getStoresUpperCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        Boolean expResult = false;
        Boolean result = instance.getStoresUpperCaseIds();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStoresUpperCaseIds method, of class IdentifierMetaData.
     */
    @Test
    public void testSetStoresUpperCaseIds() {
        System.out.println("setStoresUpperCaseIds");
        IdentifierMetaData instance = new IdentifierMetaData();
        instance.setStoresUpperCaseIds(true);
        assertEquals(true, instance.getStoresUpperCaseIds());
        assertEquals(false, instance.getStoresMixedCaseIds());
        assertEquals(false, instance.getStoresLowerCaseIds());
        instance.setStoresUpperCaseIds(false);
        assertEquals(false, instance.getStoresUpperCaseIds());
    }

    /**
     * Test of convertId method, of class IdentifierMetaData.
     * 
     * This method is disabled.
     * The test verifies that return value = input
     * without converting it.
     */
    @Test
    public void testConvertId() {
        System.out.println("convertId");

        String identifier = "MyColumnName";
        IdentifierMetaData instance = new IdentifierMetaData();
        instance.setStoresLowerCaseIds(true);
        assertEquals("MyColumnName", instance.convertId(identifier));
        instance.setStoresUpperCaseIds(true);
        assertEquals("MyColumnName", instance.convertId(identifier));
        instance.setStoresMixedCaseIds(true);
        assertEquals("MyColumnName", instance.convertId(identifier));
    }
}
