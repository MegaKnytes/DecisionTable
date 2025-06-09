package org.megaknytes.ftc.decisiontable.core.utils;

import android.content.Context;
import android.os.Environment;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.DecisionTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTFileDiscovery {
    public static Map<String, DecisionTable> getEnabledDecisionTables(Context context) throws ParserConfigurationException {
        Map<String, DecisionTable> enabledTables = new HashMap<>();

        File decisionTablesUserDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            throw new RuntimeException("Unable to access app context directory");
        }

        if (!decisionTablesUserDir.exists()) {
            decisionTablesUserDir.mkdirs();
        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File[] xmlUserDirFiles = decisionTablesUserDir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml"));
        File[] xmlAppContextFiles = appContextDir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml"));

        File[] allFileSources = Stream.concat(
                xmlUserDirFiles != null ? Arrays.stream(xmlUserDirFiles) : Stream.empty(),
                xmlAppContextFiles != null ? Arrays.stream(xmlAppContextFiles) : Stream.empty()
        ).toArray(File[]::new);

        for (File xmlFile : allFileSources) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (doc.getDocumentElement().getNodeName().equals("DecisionTable")) {
                    NodeList configNodes = doc.getElementsByTagName("Configuration");
                    if (configNodes.getLength() > 0) {
                        Element configElement = (Element) configNodes.item(0);

                        String tableName = getElementTextContent(configElement, "Name");
                        if (tableName.isEmpty()) {
                            tableName = xmlFile.getName().replace(".xml", "");
                        }

                        String enabledValue = getElementTextContent(configElement, "Enabled");
                        if ("true".equalsIgnoreCase(enabledValue)) {
                            if (enabledTables.containsKey(tableName)) {
                                throw new ConfigurationException("Duplicate decision table name: " + tableName);
                            }
                            String flavourValue = getElementTextContent(configElement, "Type");
                            String transitionTarget = getElementTextContent(configElement, "TransitionTarget");
                            assert flavourValue != null;
                            enabledTables.put(tableName, new DecisionTable(xmlFile, OpModeMeta.Flavor.valueOf(flavourValue.toUpperCase()), transitionTarget));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error parsing file: " + xmlFile.getName() + " - " + e.getMessage());
            }
        }

        return enabledTables;
    }

    private static String getElementTextContent(Element parent, String tagName) {
        NodeList elements = parent.getElementsByTagName(tagName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return null;
    }
}