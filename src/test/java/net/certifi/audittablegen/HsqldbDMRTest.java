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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.*;
import static org.junit.Assert.*;
import static org.fest.assertions.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Glenn Sacks
 */
public class HsqldbDMRTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HsqldbDMRTest.class);
    
    HsqldbDMR hsqldbDMR;
    //BasicDataSource dataSource = mock(BasicDataSource.class);
    BasicDataSource dataSource = new BasicDataSource();
    
    public HsqldbDMRTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:aname");
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        try {
            hsqldbDMR = new HsqldbDMR(dataSource);
        } catch (SQLException ex) {
            logger.error("failed to create dataSource for test class: " + ex.getMessage());
        }
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of createTestTable method, of class HsqldbDMR.
     */
//    @Test
    public void testCreateTestTable() {
        System.out.println("CreateTestTable");
        //doThrow(new RuntimeException()).when(dataSource).getConnection();
        HsqldbDMR instance = hsqldbDMR ;
        try {
            instance.createTestTable();
        } catch (SQLException ex) {
            fail("CreateTestTable threw exception");
        }
    }

    /**
     * Test of selectTestRow method, of class HsqldbDMR.
     */
//    @Test
    public void testSelectTestRow() {
        System.out.println("SelectTestRow");
        HsqldbDMR instance = hsqldbDMR;
        instance.selectTestRow();

    }

    /**
     * Test of printDataSourceStats method, of class HsqldbDMR.
     */
//    @Test
    public void testPrintDataSourceStats() {
        System.out.println("printDataSourceStats");
        //DataSource ds = null;
        hsqldbDMR.printDataSourceStats();

    }

    /**
     * Test of shutdownDataSource method, of class HsqldbDMR.
     */
//    @Test
    
       /**
     * Test methods in order, of class HsqldbDMR.
     */
    @Test
    public void testAllMethodsInOrder() throws Exception {
        testCreateTestTable();
        testSelectTestRow();
        testPrintDataSourceStats();
    }

    @Test
    public void testGetRunTimeDataSource_0args() throws SQLException {
        logger.error("****************************************************");
        DataSource ds = HsqldbDMR.getRunTimeDataSource();
        assertThat(ds.getConnection().getSchema()).isEqualTo("PUBLIC");
        logger.trace("Got connection to schema: {}", ds.getConnection().getSchema());
    }

    @Test
    public void testGetRunTimeDataSource_Properties() {
    }

    @Test
    public void testEnsureConnection() {
    }
}
