package org.megaknytes.ftc.decisiontable.core.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XMLUtils {
    
    public static Element getFirstChildElementByName(Element parent, String name) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name)) {
                return (Element) child;
            }
        }
        return null;
    }

    public static boolean hasParameterElement(Element parent) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    child.getNodeName().equals("Parameter")) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasChildElements(Element parent) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    public static List<Element> getElementNodes(NodeList nodeList) {
        List<Element> elements = new ArrayList<>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return elements;
    }
}
