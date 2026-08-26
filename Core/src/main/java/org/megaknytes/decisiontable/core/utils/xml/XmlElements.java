package org.megaknytes.decisiontable.core.utils.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public final class XmlElements {
    public static List<Element> childElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) child);
            }
        }

        return elements;
    }

    public static List<Element> childElementsNamed(Element parent, String tagName) {
        List<Element> elements = new ArrayList<>();

        for (Element child : childElements(parent)) {
            if (child.getTagName().equals(tagName)) {
                elements.add(child);
            }
        }

        return elements;
    }

    public static String optionalAttribute(Element element, String name) {
        return element.hasAttribute(name) ? element.getAttribute(name) : null;
    }
}