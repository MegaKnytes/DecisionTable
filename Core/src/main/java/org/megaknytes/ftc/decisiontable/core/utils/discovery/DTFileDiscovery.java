package org.megaknytes.ftc.decisiontable.core.utils.discovery;

import android.content.Context;
import android.os.Environment;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTFileDiscovery {
    private static final Logger LOGGER = Logger.getLogger(DTFileDiscovery.class.getName());

    public static File[] getDeviceXMLFiles(Context context) {
        LOGGER.log(Level.INFO, "Beginning to scan for device XML files...");

        File userDataDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            LOGGER.log(Level.SEVERE, "Error: Unable to access app context directory while scanning for device XML files");
            throw new RuntimeException("Unable to access app context directory while scanning for device XML files");
        }

        if (!userDataDir.exists() && !userDataDir.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Error: Failed to create user data directory while scanning for device XML files");
            throw new RuntimeException("Failed to create user data directory while scanning for device XML files");
        }

        return Stream.of(userDataDir, appContextDir)
                .filter(File::exists)
                .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml")))))
                .toArray(File[]::new);
    }

    public static Map<String, SystemConfiguration> getEnabledSystemConfigurations(File[] files) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled system configurations...");

        Map<String, SystemConfiguration> enabledSystemConfigurations = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (doc.getDocumentElement().getNodeName().equals("SystemConfiguration")) {
                    LOGGER.log(Level.INFO, "Processing system configuration file: " + xmlFile.getAbsolutePath());

                    NodeList configNodes = doc.getElementsByTagName("Configuration");
                    if (configNodes.getLength() > 0) {
                        Element configElement = (Element) configNodes.item(0);
                        String systemConfigurationName = XMLHelperMethods.getElementTextContent(configElement, "Name");

                        if (systemConfigurationName == null || systemConfigurationName.isEmpty()) {
                            LOGGER.log(Level.WARNING, "Configuration name is empty, using file name as system configuration name: " + xmlFile.getName());
                            systemConfigurationName = xmlFile.getName().replace(".xml", "");
                        }

                        String enabledValue = XMLHelperMethods.getElementTextContent(configElement, "Enabled");
                        if ("true".equalsIgnoreCase(enabledValue)) {
                            if (enabledSystemConfigurations.containsKey(systemConfigurationName)) {
                                LOGGER.log(Level.SEVERE, "Duplicate system configuration name found: " + systemConfigurationName);
                                throw new ConfigurationException("Duplicate system configuration name: " + systemConfigurationName);
                            }
                            try {
                                enabledSystemConfigurations.put(systemConfigurationName, new SystemConfiguration(xmlFile));
                            } catch (IllegalArgumentException e) {
                                LOGGER.log(Level.SEVERE, "Invalid decision table type in file: " + xmlFile.getAbsolutePath(), e);
                                throw new ConfigurationException("Invalid decision table type: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException | SAXException e) {
                LOGGER.log(Level.SEVERE, "Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
                throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            }
        }

        return enabledSystemConfigurations;
    }

    public static Map<String, Ruleset> getEnabledRulesets(File[] files, Map<String, SystemConfiguration> enabledSystemConfigurations) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled rulesets...");

        Map<String, Ruleset> enabledRulesets = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (doc.getDocumentElement().getNodeName().equals("DecisionTable")) {
                    LOGGER.log(Level.INFO, "Processing decision table file: " + xmlFile.getAbsolutePath());

                    NodeList configNodes = doc.getElementsByTagName("Configuration");
                    if (configNodes.getLength() > 0) {
                        Element configElement = (Element) configNodes.item(0);

                        String tableName = XMLHelperMethods.getElementTextContent(configElement, "Name");

                        if (tableName == null || tableName.isEmpty()) {
                            LOGGER.log(Level.WARNING, "Decision table name is empty, using file name as table name: " + xmlFile.getName());
                            tableName = xmlFile.getName().replace(".xml", "");
                        }

                        String enabledValue = XMLHelperMethods.getElementTextContent(configElement, "Enabled");
                        if ("true".equalsIgnoreCase(enabledValue)) {
                            if (enabledRulesets.containsKey(tableName)) {
                                String newTableName = tableName + "_" + System.currentTimeMillis();
                                LOGGER.log(Level.SEVERE, "Duplicate decision table name found: " + tableName
                                        + ". Renaming to: " + newTableName);
                                RobotLog.addGlobalWarningMessage("Duplicate decision table name found: " + tableName
                                        + ". Renaming to: " + newTableName);
                                tableName = newTableName;
                            }
                            try {
                                String transitionTarget = XMLHelperMethods.getElementTextContent(configElement, "TransitionTarget");
                                String systemConfigurationName = XMLHelperMethods.getElementTextContent(configElement, "SystemConfiguration");

                                String flavourValue = XMLHelperMethods.getElementTextContent(configElement, "Type");
                                SystemConfiguration systemConfiguration = enabledSystemConfigurations.get(systemConfigurationName);

                                if (systemConfiguration == null) {
                                    LOGGER.log(Level.SEVERE, "System configuration not found: " + systemConfigurationName);
                                    throw new ConfigurationException("System configuration not found: " + systemConfigurationName);
                                }

                                if (flavourValue == null) {
                                    LOGGER.log(Level.SEVERE, "Decision table configuration missing required element: Type");
                                    throw new ConfigurationException("Decision table configuration missing required element: Type");
                                }

                                enabledRulesets.put(tableName, new Ruleset(xmlFile, systemConfiguration, OpModeMeta.Flavor.valueOf(flavourValue.toUpperCase()), transitionTarget));
                            } catch (IllegalArgumentException e) {
                                LOGGER.log(Level.SEVERE, "Invalid decision table type in file: " + xmlFile.getAbsolutePath(), e);
                                throw new ConfigurationException("Invalid decision table type: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException | SAXException e) {
                LOGGER.log(Level.SEVERE, "Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
                throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            }
        }

        return enabledRulesets;
    }
}