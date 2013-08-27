/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.sql.*;
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
public class GenericDMRTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenericDMRTest.class);
    //GenericDMR dmr = mock(GenericDMR.class);
    GenericDMR dmr;
    JDBCDataSource dataSource;
    DatabaseMetaData dmd;
    
    public GenericDMRTest() {
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
            
            Connection conn = dataSource.getConnection();
            dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, "AUDITCONFIG", null);
            while (rs.next()){
                if (rs.getString("TABLE_NAME").equalsIgnoreCase("auditconfig")){
                    logger.info ("Validating test setup - Audit Configuration created");
                }
            }
            
        } catch (SQLException e){
            logger.error("error setting up unit tests: " + e.getMessage());
        } catch (LiquibaseException le){
            logger.error("liquibase error" + le.getMessage());
        }
        
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setSchemaName method, of class GenericDMR.
     */
    @Test
    public void testSetSchemaName() {
        System.out.println("setSchemaName");
        String schema = "PUBLIC";
        GenericDMR instance = dmr;
        instance.setSchemaName(schema);
        instance.getConnection();
        assertEquals(instance.targetSchema, schema);       
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
     * Test of hasConfigSource method, of class GenericDMR.
     */
    @Test
    public void testLoadConfigSource() {
        System.out.println("loadConfigSource");

        Boolean expResult = true;
        Boolean result = dmr.hasConfigSource();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetColumnMetaDataForTable() {
        Map columnMetaDataForTable = dmr.getColumnMetaDataForTable("AUDITCONFIG");
        logger.debug("");
    }
}
