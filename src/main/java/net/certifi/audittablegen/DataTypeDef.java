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

import java.text.MessageFormat;

/**
 * Class designed to store the contents of DatabaseMetaData.getTypeInfo() result
 * set.
 * 
 * @author Glenn Sacks
 */
public class DataTypeDef {
    
    // local definition // meta data description // meta data result set column name
    String type_name; //TYPE_NAME 
    int data_type; // SQL data type from java.sql.Types //DATA_TYPE 
    int precision; // maximum precision //PRECISION 
    String literal_prefix; // prefix used to quote a literal (may be null) //LITERAL_PREFIX 
    String literal_suffix; // suffix used to quote a literal (may be null) //LITERAL_SUFFIX 
    String create_params; // parameters used in creating the type (may be null) //CREATE_PARAMS 
    short nullable;   // can you use NULL for this type. //NULLABLE 
                      // - does not allow NULL values //typeNoNulls
                      // - allows NULL values //typeNullable
                      // - nullability unknown //typeNullableUnknown
    boolean case_sensitive; // is it case sensitive. //CASE_SENSITIVE 
    short searchable; // can you use "WHERE" based on this type: //SEARCHABLE 
                      // - No support //typePredNone 
                      // - Only supported with WHERE .. LIKE //typePredChar
                      // - Supported except for WHERE .. LIKE //typePredBasic 
                      // - Supported for all WHERE .. //typeSearchable 
    boolean unsigned_attribute; // is it unsigned. //UNSIGNED_ATTRIBUTE 
    boolean fixed_prec_scale; // can it be a money value. //FIXED_PREC_SCALE 
    boolean auto_increment; // can it be used for an auto-increment value. //AUTO_INCREMENT 
    String local_type_name; // localized version of type name (may be null) //LOCAL_TYPE_NAME 
    short minimum_scale; // minimum scale supported //MINIMUM_SCALE 
    short maximum_scale; // maximum scale supported //MAXIMUM_SCALE 
    int sql_data_type; // unused //SQL_DATA_TYPE 
    int sql_datetime_sub; // unused //SQL_DATETIME_SUB 
    int num_prec_radix; // usually 2 or 10 //NUM_PREC_RADIX
    
    //additional params - not from MetaData
    boolean createWithSize;
    
    static int[] sqlSizedTypes = {
        java.sql.Types.CHAR,
        java.sql.Types.VARCHAR,
        java.sql.Types.DECIMAL,
        java.sql.Types.FLOAT,
        java.sql.Types.NCHAR,
        java.sql.Types.NUMERIC,
        java.sql.Types.NVARCHAR,
        java.sql.Types.VARBINARY,
        java.sql.Types.VARCHAR};
       
    @Override
    public String toString(){
        
        MessageFormat fmt = new MessageFormat("TYPE_NAME={0}, DATA_TYPE={1}, PRECISION={2}, LITERAL_PREFIX={3},"
                + " LITERAL_SUFFIX={4}, CREATE_PARAMS={5}, NULLABLE={6},CASE_SENSITIVE={7}, SEARCHABLE={8},"
                + " UNSIGNED_ATTRIBUTE={9} FIXED_PREC_SCALE={10}, AUTO_INCREMENT={11}, LOCAL_TYPE_NAME={12}, MINIMUM_SCALE={13},"
                + " MAXIMUM_SCALE={14}, SQL_DATA_TYPE={15}, SQL_DATATIME_SUB={16}, NUM_PREC_RADIX={17}");
        
        Object[] objs = {type_name, data_type, precision, literal_prefix, literal_suffix, create_params, nullable, case_sensitive,
                searchable, unsigned_attribute, fixed_prec_scale, auto_increment, local_type_name, minimum_scale, maximum_scale,
                sql_data_type, sql_datetime_sub, num_prec_radix};
        
        return fmt.format(objs);
        
    }
}
