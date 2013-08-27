/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  This is a work in progress.  It does not build an actual change file yet.
 * 
 * @author Glenn Sacks
 */
public class ChangeFileBuilder {
    
       /**
     * create audit configuration table script in output path
     * this is just silly stuff - think I'm doing this backwards...
     */
    void createConfigSource (String filepath){
        
        Element de = null;
        String fileStream;
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
 
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
            // root elements
	    Document dom = docBuilder.newDocument();
	    Element rootElement = dom.createElement("databaseChangeLog");
            
            rootElement.setAttribute("logicalFilePath", filepath);
//            rootElement.setAttributeNS("http://www.liquibase.org/xml/ns", "xmlns", "http://www.liquibase.org/xml/ns/dbchangelog");
//            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            
             Element changeset = dom.createElement("ChangeSet");
             changeset.setAttribute("author", "AuditTableGen");
             changeset.setAttribute("id", "generated-" + DateTime.now().toString());
             rootElement.appendChild(changeset);
             
             Element table = dom.createElement("createTable");
             table.setAttribute("tableName", "auditconfig");
             changeset.appendChild(table);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer tr = transformerFactory.newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            DOMSource source = new DOMSource(dom);
            
            StreamResult result = new StreamResult(new File("C:\\file.xml")); //or System.out
            tr.transform(source, result);
            
        } catch  (TransformerException te) {
            System.out.println(te.getMessage());
        } catch (ParserConfigurationException pce) {
        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
    }
    }
}
