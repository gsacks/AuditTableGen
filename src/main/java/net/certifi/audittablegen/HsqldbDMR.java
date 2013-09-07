/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import ch.qos.logback.classic.Level;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
//import org.hsqldb.Statement;
import org.hsqldb.jdbc.JDBCDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class HsqldbDMR extends GenericDMR {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HsqldbDMR.class);
    
    HsqldbDMR(DataSource ds) throws SQLException {

        super(ds);

    }

    HsqldbDMR (DataSource ds, String schema) throws SQLException{
        
        super(ds, schema);
        
    }
    
    /**
     * Generate an in memory hsqldb datasource for testing
     *
     * @return BasicDataSource as DataSource
     */
    static DataSource getRunTimeDataSource() {
        //        BasicDataSource dataSource = new BasicDataSource();
        JDBCDataSource dataSource = new JDBCDataSource();
//        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
//        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:aname");
//        dataSource.setMaxActive(10);
//        dataSource.setMaxIdle(5);
//        dataSource.setInitialSize(5);
//        dataSource.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        return dataSource;

    }

    /**
     * Generate a Hsqldb DataSource from Properties
     *
     * @param props
     * @return BasicDataSource as DataSource
     */
    static DataSource getRunTimeDataSource(Properties props) {

        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setAccessToUnderlyingConnectionAllowed(true);
        dataSource.setValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        return dataSource;
    }

    void createTestTable() throws SQLException {

       //logger.debug("dataSourse is NOT null: {}", dmd.getURL());

        String SQL = "Create table test1 (test1Id integer not null identity, test1Data integer  )";
        try {
            Connection conn = dataSource.getConnection();
            logger.trace("here1");
            if (dataSource == null) {
                logger.warn("dataSourse is null");
                logger.trace("here2");
            } else {
                logger.debug("dataSourse is NOT null: {}", dataSource.toString());
                logger.trace("here3");
            }

            Statement stmt = conn.createStatement();

            if (stmt == null) {
                logger.warn("stmt is null");
            } else {
                logger.debug("stmt is NOT null");
            }


            stmt.executeUpdate(SQL);

            stmt.executeUpdate("insert into test1 (test1Data) values 1");

            stmt.close();

            conn.close();

        } catch (SQLException ex) {
            logger.error("Error ...", ex);
        }

    }

    void selectTestRow() {

        try {
            Connection conn = dataSource.getConnection();

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from test1");

            while (rs.next()) {
                String id = rs.getString("test1Id");
                String data = rs.getString("test1Data");
                logger.debug("Id: {}  Data: {}", id, data);
            }
        } catch (SQLException ex) {
            logger.error("Error selecting test row", ex);
        }

    }

    public void printDataSourceStats() {
        try {
            if (dataSource.isWrapperFor(BasicDataSource.class)) {
                BasicDataSource bds = dataSource.unwrap(BasicDataSource.class);
                logger.debug("NumActive: {}", bds.getNumActive());
                logger.debug("NumIdle: {}", bds.getNumIdle());

            } else {
                logger.warn("DataSource Stats not available");
            }
        } catch (SQLException ex) {
            logger.error("Error getting stats", ex);
        }

    }

    /**
     * When KeepOpen is true, ensures that the connection is working. When KeepOpen is false, attempts a temporary
     * connection to validate the DataSource, but does not keep it open.
     *
     * @return true if a connection to the source can be established false if a connection cannot be established
     */
    //@Override
    public boolean ensureConnection() {

        Connection conn;

        try {
            conn = dataSource.getConnection();
            if (conn != null && conn.isValid(15)) {
                conn.close();
                return true;
            }

        } catch (SQLException ex) {
            logger.error("Error ensuring connection", ex);
        }

        return false;
    }
}
