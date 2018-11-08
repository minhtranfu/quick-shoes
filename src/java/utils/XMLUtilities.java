/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author MinhTran
 */
public class XMLUtilities implements Serializable {

    public static Document parseXMLToDOM(String xml) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document document = builder.parse(is);

        return document;
    }

    public static Document parseFileToDom(String xmlFilePath) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(xmlFilePath);
    }

    public static XPath createXpath() {

        XPathFactory factory = XPathFactory.newInstance();

        return factory.newXPath();
    }

    public static boolean validateXML(String xml, String xsdPath) {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;

        try {
            schema = factory.newSchema(new File(xsdPath));
        } catch (Exception e) {
            Logger.getLogger(XMLUtilities.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }

        try {
            Validator validator = schema.newValidator();
            Source is = new StreamSource(new StringReader(xml));
            validator.validate(is);
        } catch (Exception e) {
            Logger.getLogger(XMLUtilities.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }

        return true;
    }
}
