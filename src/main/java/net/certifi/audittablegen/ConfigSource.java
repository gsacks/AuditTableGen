/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Glenn Sacks
 */
public class ConfigSource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigSource.class);
    
    //Map<String, TableConfig> existingTables;
    //Map<String, TableConfig> existingAuditTables;

    List<TableDef> allTables; //list of all tables in the tartget db/schema
    List<ConfigAttribute> excludes; //only exclude attributes
    List<ConfigAttribute> includes; //only include attributes
    List<ConfigAttribute> dbAttribs; //prefixes & postfixes
    List<ConfigAttribute> triggerAttribs; //triggers
    List<ConfigAttribute> otherAttributes; //these are of unknown type
    //IdentifierMetaData idMetaData;
    int maxUserNameLength;

    
    ConfigSource(){
        //this.idMetaData = idMetaData;
        //existingTables = new CaseInsensitiveMap();
        //existingAuditTables = new CaseInsensitiveMap();
        dbAttribs = new ArrayList<>();
        triggerAttribs = new ArrayList<>();
        excludes = new ArrayList<>();
        includes = new ArrayList<>();
        otherAttributes = new ArrayList<>();
                
        allTables = new ArrayList<>();

    }
    
    void addAttributes(List<ConfigAttribute> attributes){
        
        for ( ConfigAttribute attrib : attributes){
            
            addAttribute(attrib);
            
        }
    }

    void addAttribute(ConfigAttribute attrib) {
        
        switch (attrib.getType()) {
            case exclude:
                excludes.add(attrib);
                break;
            case include:
                includes.add(attrib);
                break;
            case tableprefix: 
            case tablepostfix:
            case columnprefix:
            case columnpostfix:
                dbAttribs.add(attrib);
                break;
            case iddatatype:
                dbAttribs.add(attrib);
                break;                
            case userdatatype:
                dbAttribs.add(attrib);
                break;
            case actiondatatype:
                dbAttribs.add(attrib);
                break;
            case timestampdatatype:
                dbAttribs.add(attrib);
                break;
            case auditinsert:
            case auditupdate:
            case auditdelete:
                triggerAttribs.add(attrib);
            case unknown:
            default:
                otherAttributes.add(attrib);
                break;
        }
    }
    
    void addTable(TableDef tableDef){
        
        allTables.add(tableDef);
    }
    
    void addTables(List<TableDef> tablesDefs){
        
        allTables.addAll(tablesDefs);
        
    }

    public int getMaxUserNameLength() {
        return maxUserNameLength;
    }

    public void setMaxUserNameLength(int maxUserNameLength) {
        this.maxUserNameLength = maxUserNameLength;
    }
    
    
}
