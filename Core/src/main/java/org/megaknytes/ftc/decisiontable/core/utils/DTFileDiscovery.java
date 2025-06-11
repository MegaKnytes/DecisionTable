package org.megaknytes.ftc.decisiontable.core.utils;

import android.content.Context;
import android.os.Environment;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Ruleset;
import org.megaknytes.ftc.decisiontable.core.xml.structure.SystemConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTFileDiscovery {

    public static Map<String, SystemConfiguration> getEnabledSystemConfigurations(Context context) throws ParserConfigurationException {
        Map<String, SystemConfiguration> enabledSystemConfigurations = new HashMap<>();

        File userDataDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            throw new RuntimeException("Unable to access app context directory");
        }

        if (!userDataDir.exists() && !userDataDir.mkdirs()) {
            throw new RuntimeException("Failed to create user data directory");
        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File[] allFileSources = Stream.of(userDataDir, appContextDir)
                .filter(File::exists)
                .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml")))))
                .toArray(File[]::new);

        for (File xmlFile : allFileSources) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (doc.getDocumentElement().getNodeName().equals("SystemConfiguration")) {
                    NodeList configNodes = doc.getElementsByTagName("Configuration");
                    if (configNodes.getLength() > 0) {
                        Element configElement = (Element) configNodes.item(0);

                        String systemConfigurationName = XMLUtils.getElementTextContent(configElement, "Name");

                        if (systemConfigurationName == null || systemConfigurationName.isEmpty()) {
                            systemConfigurationName = xmlFile.getName().replace(".xml", "");
                        }

                        String enabledValue = XMLUtils.getElementTextContent(configElement, "Enabled");
                        if ("true".equalsIgnoreCase(enabledValue)) {
                            if (enabledSystemConfigurations.containsKey(systemConfigurationName)) {
                                throw new ConfigurationException("Duplicate decision table name: " + systemConfigurationName);
                            }
                            try {
                                enabledSystemConfigurations.put(systemConfigurationName, new SystemConfiguration(xmlFile));
                            } catch (IllegalArgumentException e) {
                                throw new ConfigurationException("Invalid decision table type: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException | SAXException e) {
                throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            }
        }

        return enabledSystemConfigurations;
    }

    public static Map<String, Ruleset> getEnabledRulesets(Context context, Map<String, SystemConfiguration> enabledSystemConfigurations) throws ParserConfigurationException {
        Map<String, Ruleset> enabledTables = new HashMap<>();

        File userDataDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            throw new RuntimeException("Unable to access app context directory");
        }

        if (!userDataDir.exists() && !userDataDir.mkdirs()) {
            throw new RuntimeException("Failed to create user data directory");
        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File[] allFileSources = Stream.of(userDataDir, appContextDir)
                .filter(File::exists)
                .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml")))))
                .toArray(File[]::new);

        for (File xmlFile : allFileSources) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (doc.getDocumentElement().getNodeName().equals("DecisionTable")) {
                    NodeList configNodes = doc.getElementsByTagName("Configuration");
                    if (configNodes.getLength() > 0) {
                        Element configElement = (Element) configNodes.item(0);

                        String tableName = XMLUtils.getElementTextContent(configElement, "Name");

                        if (tableName == null || tableName.isEmpty()) {
                            tableName = xmlFile.getName().replace(".xml", "");
                        }

                        String enabledValue = XMLUtils.getElementTextContent(configElement, "Enabled");
                        if ("true".equalsIgnoreCase(enabledValue)) {
                            if (enabledTables.containsKey(tableName)) {
                                throw new ConfigurationException("Duplicate decision table name: " + tableName);
                            }
                            try {
                                String transitionTarget = XMLUtils.getElementTextContent(configElement, "TransitionTarget");
                                String systemConfigurationName = XMLUtils.getElementTextContent(configElement, "SystemConfigurationName");

                                String flavourValue = XMLUtils.getElementTextContent(configElement, "Type");
                                SystemConfiguration systemConfiguration = enabledSystemConfigurations.get(systemConfigurationName);

                                if (systemConfiguration == null) {
                                    throw new ConfigurationException("System configuration not found: " + systemConfigurationName);
                                }

                                if (flavourValue == null) {
                                    throw new ConfigurationException("Decision table configuration missing required element: Type");
                                }

                                enabledTables.put(tableName, new Ruleset(xmlFile, systemConfiguration, OpModeMeta.Flavor.valueOf(flavourValue.toUpperCase()), transitionTarget));
                            } catch (IllegalArgumentException e) {
                                throw new ConfigurationException("Invalid decision table type: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException | SAXException e) {
                throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            }
        }

        return enabledTables;
    }
}