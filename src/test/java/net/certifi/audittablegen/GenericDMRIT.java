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

import java.sql.*;
import java.util.Iterator;
import java.util.List;
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
    
    /**
     * Test of getColumns method, of class GenericDMR.
     */
    @Test
    public void testGetColumns() throws SQLException {
        System.out.println("getColumns");

        String tableName = "auditconfigattribute";
        String verifiedSchema = "PUBLIC";

        dmr.verifiedSchema = verifiedSchema;
                
        List result = dmr.getColumns(tableName);
        


        assertEquals(9, result.size());

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
