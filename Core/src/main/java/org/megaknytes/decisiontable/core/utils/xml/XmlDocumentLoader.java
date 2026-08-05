package org.megaknytes.decisiontable.core.utils.xml;

import org.megaknytes.decisiontable.core.utils.exceptions.XmlParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlDocumentLoader {
    public Document parse(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            return document;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new XmlParseException("Failed to parse " + file.getName() + ": " + e.getMessage());
        }
    }
}
