/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import com.google.common.base.Throwables;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.dbcp.BasicDataSource;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class PostgresqlDMR extends GenericDMR {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditTableGen.class);

    PostgresqlDMR (DataSource ds) throws SQLException{
        
        super(ds);
        
    }
    
    PostgresqlDMR (DataSource ds, String schema) throws SQLException{
        
        super(ds, schema);
        
    }
        
    /**
     * Generate a Postgresql DataSource from Properties 
     * @param props
     * @return BasicDataSource as DataSource
     */
    static DataSource getRunTimeDataSource(Properties props){
        
        //BasicDataSource dataSource = new BasicDataSource();
        //PGPoolingDataSource dataSource = new PGPoolingDataSource();
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        int port;
        dataSource.setUser(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        //dataSource.setInitialConnections(2);
        dataSource.setApplicationName("AuditTableGen");
        dataSource.setServerName(props.getProperty("server"));
        dataSource.setDatabaseName(props.getProperty("database"));
        if (props.containsKey("port")){
            port = Integer.getInteger(props.getProperty("port", "5432"));
            dataSource.setPortNumber(port);
        }
        
        //dataSource.setDriverClassName("org.postgresql.Driver");
        //dataSource.setUsername(props.getProperty("username"));
        //dataSource.setPassword(props.getProperty("password"));
        //dataSource.setUrl(props.getProperty("url"));
        //dataSource.setMaxActive(10);
        //dataSource.setMaxIdle(5);
        //dataSource.setInitialSize(5);
        //dataSource.setValidationQuery("SELECT 1");
        
        return dataSource;
    }
            
    public void printDataSourceStats() {
        try {
            if (dataSource.isWrapperFor(BasicDataSource.class)){
                BasicDataSource bds = dataSource.unwrap(BasicDataSource.class);
                System.out.println("NumActive: " + bds.getNumActive());
                System.out.println("NumIdle: " + bds.getNumIdle());
                
            }
            else {
                System.out.println ("DataSource Stats not available");
            }
        } catch (SQLException ex) {
            logger.error("Error getting DataSource stats:" + ex.getMessage() );
        }
        
    }
    
    /**
     * Get List of ColumnDef objects for all tables
     * in the targeted database/schema.  Postgres specific code replaces 
     * 'serial' date type with integer, because the column in the audit table
     * must be of type integer and not serial.  Since this data is interpreted
     * by ChangeSourceFactory, which should be database independent, the
     * translation needs to be in the DMR.
     * 
     * @param tableName
     * @return ArrayList of ColumnDef objects or an empty list if none are found.
     */
    @Override
    public List getColumns (String tableName){
        
        //getDataTypes will initialize the map if it isn't already loaded
        Map<String, DataTypeDef> dtds = getDataTypes();
        
        List columns = new ArrayList<>();
        
        try {
            Connection conn = dataSource.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getColumns(null, verifiedSchema, tableName, null);
            
            //load all of the metadata in the result set into a map for each column
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int metaDataColumnCount = rsmd.getColumnCount();
            if (! rs.isBeforeFirst()) {
                throw new RuntimeException("No results for DatabaseMetaData.getColumns(" + verifiedSchema + "." + tableName + ")");
            }
            while (rs.next()){
                ColumnDef columnDef = new ColumnDef();
                Map columnMetaData = new CaseInsensitiveMap();
                for (int i = 1; i <= metaDataColumnCount; i++){
                    columnMetaData.put(rsmd.getColumnName(i), rs.getString(i));
                }
                columnDef.setName(rs.getString("COLUMN_NAME"));
                
                String type_name = rs.getString("TYPE_NAME");
                if ( type_name.equalsIgnoreCase("serial")){
                    columnDef.setTypeName("integer");
                }
                else {
                    columnDef.setTypeName(type_name);
                }
                columnDef.setSqlType(rs.getInt("DATA_TYPE"));
                columnDef.setSize(rs.getInt("COLUMN_SIZE"));
                columnDef.setDecimalSize(rs.getInt("DECIMAL_DIGITS"));
                columnDef.setSourceMeta(columnMetaData);
                
                if (dtds.containsKey(columnDef.getTypeName())){
                    columnDef.setDataTypeDef(dtds.get(columnDef.getTypeName()));
                }
                else {
                    throw new RuntimeException("Missing DATA_TYPE definition for data type " + columnDef.getTypeName());
                }                
                columns.add(columnDef);
            }
            
        }
        catch (SQLException e) {
            throw Throwables.propagate(e);
        }
        
        return columns;
        
    }
    
}
