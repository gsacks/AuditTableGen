/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Glenn Sacks
 */
public class TableDefTest {
    
    public TableDefTest() {
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
     * Test of addColumn method, of class TableDef.
     */
    @Test
    public void testAddColumn() {
        System.out.println("addColumn");
        //ColumnDef cd = new ColumnDef();
        TableDef instance = new TableDef();
        instance.addColumn( new ColumnDef());
        instance.addColumn( new ColumnDef());
        assertEquals(2,instance.columns.size());

    }

    /**
     * Test of getName method, of class TableDef.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        TableDef instance = new TableDef();
        instance.name = "testTable";
        String expResult = "testTable";
        String result = instance.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of setName method, of class TableDef.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "test";
        TableDef instance = new TableDef();
        instance.setName(name);
        assertEquals(name, instance.name);
    }

    /**
     * Test of getColumns method, of class TableDef.
     */
    @Test
    public void testGetColumns() {
        System.out.println("getColumns");
        TableDef instance = new TableDef();
        List<ColumnDef> expResult = new ArrayList<>();
        expResult.add(new ColumnDef());
        instance.columns = expResult;
        List result = instance.getColumns();
        assertEquals(expResult, result);
 
    }

    /**
     * Test of setColumns method, of class TableDef.
     */
    @Test
    public void testSetColumns() {
        System.out.println("setColumns");
        List<ColumnDef> columns = new ArrayList<>();
        columns.add((new ColumnDef()));
        TableDef instance = new TableDef();
        instance.setColumns(columns);
        assertEquals(columns, instance.columns);

    }
}
