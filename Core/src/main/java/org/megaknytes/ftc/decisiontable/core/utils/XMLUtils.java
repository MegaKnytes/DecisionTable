package org.megaknytes.ftc.decisiontable.core.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {
    public static Element getFirstChildElement(Element parent) {
        NodeList children = parent.getChildNodes();
        for (int childCount = 0; childCount < children.getLength(); childCount++) {
            if (children.item(childCount).getNodeType() == Node.ELEMENT_NODE) {
                return (Element) children.item(childCount);
            }
        }
        return null;
    }

    public static Element getFirstChildElementByName(Element parent, String name) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name)) {
                return (Element) child;
            }
        }
        return null;
    }

    public static boolean hasChildElement(Element parent) {
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    child.getNodeName().equals("Parameter")) {
                return true;
            }
        }
        return false;
    }
}
