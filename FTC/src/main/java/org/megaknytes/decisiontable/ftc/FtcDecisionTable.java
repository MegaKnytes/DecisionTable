package org.megaknytes.decisiontable.ftc;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.SystemConfiguration;
import org.megaknytes.decisiontable.core.parse.SystemConfigurationParser;
import org.megaknytes.decisiontable.core.parse.DecisionTableParser;
import org.megaknytes.decisiontable.core.EvaluationResult;
import org.megaknytes.decisiontable.core.DecisionTable;
import org.megaknytes.decisiontable.core.rule.value.ValueParserRegistry;
import org.megaknytes.decisiontable.ftc.discovery.ClassDiscovery;
import org.megaknytes.decisiontable.ftc.discovery.FileDiscovery;
import org.megaknytes.decisiontable.ftc.drivers.FtcDeviceFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

public class FtcDecisionTable {
    private static final FtcDecisionTable INSTANCE = new FtcDecisionTable();
    private static final Logger LOGGER = Logger.getLogger(FtcDecisionTable.class.getName());

    private final Map<String, Device> availableDeviceDrivers = ClassDiscovery.getDriverInstances();
    private final ValueParserRegistry valueParserRegistry = ClassDiscovery.getValueParserRegistry();

    private Map<String, DecisionTableFile> enabledDecisionTables = new HashMap<>();

    private DecisionTable currentTable;
    private SystemConfiguration currentSystemConfiguration;
    private OpMode currentOpMode;

    public FtcDecisionTable() {
    }

    @OnCreateEventLoop
    public static void scanForFiles(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled system configurations and rulesets...");

        try {
            File[] deviceXMLFiles = FileDiscovery.getDeviceXMLFiles(context);
            Map<String, File> enabledSystemConfigurations = FileDiscovery.getEnabledSystemConfigurations(deviceXMLFiles);
            LOGGER.log(Level.INFO, "Enabled System Configurations: " + enabledSystemConfigurations.keySet());
            INSTANCE.enabledDecisionTables = FileDiscovery.getEnabledDecisionTables(deviceXMLFiles, enabledSystemConfigurations);
            LOGGER.log(Level.INFO, "Enabled Decision Tables: " + INSTANCE.enabledDecisionTables.keySet());
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error during XML parsing: " + e.getMessage());
            RobotLog.addGlobalWarningMessage("DecisionTable: An error occurred during XML parsing: " + e.getMessage());
        }
    }

    @OpModeRegistrar
    public static void registerOpModes(AnnotatedOpModeManager opModeManager) {
        for (Map.Entry<String, DecisionTableFile> entry : INSTANCE.enabledDecisionTables.entrySet()) {
            if (!OpModeMeta.nameIsLegalForOpMode(entry.getKey(), false)) {
                RobotLog.addGlobalWarningMessage("Decision Table with name '"+ entry.getKey() + "' not registered, as it has an incompatible name.");
                continue;
            }

            DecisionTableFile ruleset = entry.getValue();

            opModeManager.register(
                    new OpModeMeta.Builder()
                            .setName(entry.getKey())
                            .setFlavor(OpModeMeta.Flavor.valueOf(ruleset.getFlavor().name()))
                            .setGroup("DecisionTable")
                            .setSource(OpModeMeta.Source.EXTERNAL_LIBRARY)
                            .setTransitionTarget(ruleset.getTransitionTarget())
                            .build(),
                    new OpMode() {
                        @Override
                        public void init() {
                            INSTANCE.initializeDecisionTable(this, ruleset);
                        }

                        @Override
                        public void start() {
                            resetRuntime();
                        }

                        @Override
                        public void loop() {
                            INSTANCE.update();
                        }

                        @Override
                        public void stop() {
                            reset();
                        }
                    });
        }
    }

    public void initializeDecisionTable(OpMode opMode, DecisionTableFile ruleset) {
        reset();
        currentOpMode = opMode;

        try {
            FtcDeviceFactory deviceFactory = new FtcDeviceFactory(opMode);
            SystemConfiguration systemConfiguration = new SystemConfigurationParser(availableDeviceDrivers, deviceFactory, valueParserRegistry).load(ruleset.getSystemConfiguration());

            currentSystemConfiguration = systemConfiguration;
            currentTable = new DecisionTableParser(valueParserRegistry).load(ruleset.getFile(), systemConfiguration);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to load ruleset '" + ruleset.getFile().getName() + "'", e);
            RobotLog.addGlobalWarningMessage("Failed to load ruleset '" + ruleset.getFile().getName() + "': " + e.getMessage());

            opMode.requestOpModeStop();
        }
    }

    public EvaluationResult update() {
        EvaluationResult result = null;
        if (currentTable != null && currentOpMode != null) {
            result = currentTable.evaluate(currentSystemConfiguration);
        }

        return result;
    }

    public static void reset() {
        INSTANCE.currentTable = null;
        INSTANCE.currentSystemConfiguration = null;
        INSTANCE.currentOpMode = null;
    }

    public static FtcDecisionTable getInstance() {
        return INSTANCE;
    }
}