/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;


/**
 *
 * @author Glenn Sacks
 */
public interface DataSourceDMR {
    
    /**
     * Check for existence of the audit configuration tables.  The lookup is
     * not case sensitive.
     * @return True if the audit tables are present in the
     * DataSource (and schema, if a schema is set).
     */
    Boolean hasConfigSource ();
    
    /**
     * Set the schema. The input value is recorded to the an unvalidated schema
     * variable.  It is then looked up in the database, and if there is
     * a case insensitive match, then the matched value is recorded to a
     * validated schema variable, or null if there is no match.
     * @param schema 
     */
    void setSchema(String schema);
    
    /**
     * Get the schema
     * @return Returns the validated schema name. If the schema
     * cannot be validated against the DataSource, then return value is null.
     */
    String getSchema();
    
    /**
     * Set an alternate name for the audit configuration table.  The default
     * is AuditConfig.  The value is stored case sensitive, but the lookup
     * against the database is not case sensitive.
     * @param table 
     */
    void setAuditConfigTable(String table);
    
    /**
     * Get the audit configuration table name.
     * @return If the table name cannot be validated against
     * the DataSource, then return value is null. 
     */
    String getAuditConfigTable();
    
    /**
     * Get the SQL script to update the current DataSource with
     * new audit configuration tables. Does not run the script.
     * @return 
     */
    String getCreateConfigSQL();
    
    /**
     * Execute SQL to update the current DataSource with new audit
     * configuration tables.
     */
    void executeCreateConfigSQL();
    
    /**
     * Get the SQL script to validate that the current DataSource
     * has been successfully updated with audit configuration tables.
     * Does not run the script.
     * @return 
     */
    //String getValidateCreateConfigSQL();
    
    /** Validate that the DataSource has been updated with new audit
     * configuration tables.
     * 
     * @return true if the current tables match the expected result.
     */
    Boolean validateCreateConfig();
    
    /**
     * Get the SQL script to update the current DataSource with new
     * and altered audit tables.  Does not run the script.
     * @return 
     */
    String getUpdateSQL();
    
    
    /** Execute SQL to update the current DataSource with new and
     * altered audit tables.
     */
    void executeUpdateSQL();
    
    /**
     * Get the SQL script to validate that the current DataSource
     * has been successfully updated with new and altered audit tables.
     * Does not run the scrip.t
     * @return 
     */
    //String getValidateUpdateSQL();
    
    /**
     * Validate that the DataSource has been updated with new and
     * altered audit tables.
     * 
     * @return true if the current tables match the expected result. 
     */
    Boolean validateUpdate();
    
    /**
     * Creates a ConfigSource representation of the target
     * catalog/schema necessary to generate the audit tables and triggers
     * 
     * @return Returns a ConfigSource object or null if unable to generate
     * all valid components of the configuration.
     */
    ConfigSource getConfigSource();
}
