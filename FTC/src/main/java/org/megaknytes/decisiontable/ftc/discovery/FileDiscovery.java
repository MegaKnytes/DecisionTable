package org.megaknytes.decisiontable.ftc.discovery;

import android.content.Context;
import android.os.Environment;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;
import org.megaknytes.decisiontable.ftc.DecisionTableFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class FileDiscovery {
    private static final Logger LOGGER = Logger.getLogger(FileDiscovery.class.getName());

    public static File[] getDeviceXMLFiles(Context context) {
        File userDataDir = new File(Environment.getExternalStorageDirectory(), "DecisionTables");
        File appContextDir = context.getExternalFilesDir(null);

        if (appContextDir == null) {
            LOGGER.log(Level.SEVERE, "Unable to access app context directory while scanning for device XML files");
            RobotLog.addGlobalWarningMessage("Unable to access app context directory while scanning for device XML files");
            throw new ConfigurationException("Unable to access app context directory while scanning for device XML files");
        }

        if (!userDataDir.exists() && !userDataDir.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Failed to create user data directory while scanning for device XML files");
            RobotLog.addGlobalWarningMessage("Failed to create user data directory while scanning for device XML files");
            throw new ConfigurationException("Failed to create user data directory while scanning for device XML files");
        }

        return Stream.of(userDataDir, appContextDir)
                .filter(File::exists)
                .flatMap(dir -> Arrays.stream(Objects.requireNonNull(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml")))))
                .toArray(File[]::new);
    }

    public static Map<String, File> getEnabledSystemConfigurations(File[] files) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "======== System Configuration Discovery ========");

        Map<String, File> enabledSystemConfigurations = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            Element root = parseRoot(builder, xmlFile);

            if (!root.getNodeName().equals("SystemConfiguration") || isDisabled(root, xmlFile)) {
                continue;
            }

            LOGGER.log(Level.INFO, "Processing file: " + xmlFile.getAbsolutePath());
            String name = getNameOrFallback(root, xmlFile);

            if (enabledSystemConfigurations.containsKey(name)) {
                LOGGER.log(Level.SEVERE, "Duplicate system configuration name found: " + name);
                RobotLog.addGlobalWarningMessage("Duplicate system configuration name found: " + name);
                throw new ConfigurationException("Duplicate system configuration name found: " + name);
            }

            enabledSystemConfigurations.put(name, xmlFile);
        }

        return enabledSystemConfigurations;
    }

    public static Map<String, DecisionTableFile> getEnabledDecisionTables(File[] files, Map<String, File> enabledSystemConfigurations) throws ParserConfigurationException {
        LOGGER.log(Level.INFO, "======== Ruleset Discovery ========");

        Map<String, DecisionTableFile> enabledRulesets = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (File xmlFile : files) {
            Element root = parseRoot(builder, xmlFile);

            if (!root.getNodeName().equals("DecisionTable") || isDisabled(root, xmlFile)) {
                continue;
            }

            LOGGER.log(Level.INFO, "Processing file: " + xmlFile.getAbsolutePath());

            String tableName = getNameOrFallback(root, xmlFile);

            if (enabledRulesets.containsKey(tableName)) {
                String newTableName = tableName + "_" + System.currentTimeMillis();
                LOGGER.log(Level.SEVERE, "Duplicate decision table name found: " + tableName + ". Renaming to: " + newTableName);
                RobotLog.addGlobalWarningMessage("Duplicate decision table name found: " + tableName + ". Renaming to: " + newTableName);
                tableName = newTableName;
            }

            String systemConfigurationName = root.getAttribute("systemConfiguration");
            File systemConfigurationFile = enabledSystemConfigurations.get(systemConfigurationName);

            if (systemConfigurationFile == null) {
                LOGGER.log(Level.SEVERE, "System configuration not found: " + systemConfigurationName);
                RobotLog.addGlobalWarningMessage("System configuration not found: " + systemConfigurationName);
                throw new ConfigurationException("System configuration not found: " + systemConfigurationName);
            }

            OpModeMeta.Flavor flavor;

            try {
                flavor = OpModeMeta.Flavor.valueOf(root.getAttribute("type"));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Invalid Decision Table type in file: " + xmlFile.getAbsolutePath(), e);
                RobotLog.addGlobalWarningMessage("Invalid Decision Table type in file: " + xmlFile.getAbsolutePath());
                throw new ConfigurationException("Invalid Decision Table type: " + e.getMessage());
            }

            String transitionTarget = root.hasAttribute("transitionTarget") ? root.getAttribute("transitionTarget") : null;
            enabledRulesets.put(tableName, new DecisionTableFile(xmlFile, systemConfigurationFile, flavor, transitionTarget));
        }

        return enabledRulesets;
    }

    private static Element parseRoot(DocumentBuilder builder, File xmlFile) {
        try {
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc.getDocumentElement();
        } catch (IOException | SAXException e) {
            LOGGER.log(Level.SEVERE, "Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
            RobotLog.addGlobalWarningMessage("Error parsing decision table file: " + xmlFile.getAbsolutePath());
            throw new RuntimeException("Error parsing decision table file: " + xmlFile.getAbsolutePath(), e);
        }
    }

    private static boolean isDisabled(Element root, File xmlFile) {
        String enabled = root.getAttribute("enabled");

        return !"true".equals(enabled) && !"1".equals(enabled);
    }

    private static String getNameOrFallback(Element root, File xmlFile) {
        String name = root.getAttribute("name");

        if (name == null || name.isEmpty()) {
            LOGGER.log(Level.WARNING, "Name is empty, using file name instead: " + xmlFile.getName());
            return xmlFile.getName().replace(".xml", "");
        }

        return name;
    }
}