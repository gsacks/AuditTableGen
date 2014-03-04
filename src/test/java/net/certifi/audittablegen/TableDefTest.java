/*    Copyright 2014 Certifi Inc.
 *
 *    This file is part of AuditTableGen.
 *
 *        AuditTableGen is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        (at your option) any later version.
 *
 *        AuditTableGen is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with AuditTableGen.  If not, see <http://www.gnu.org/licenses/>.
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
