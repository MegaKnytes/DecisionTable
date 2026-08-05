package org.megaknytes.decisiontable.ftc;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.config.LoadedSystemConfiguration;
import org.megaknytes.decisiontable.core.config.SystemConfigurationLoader;
import org.megaknytes.decisiontable.core.DecisionTableEvaluator;
import org.megaknytes.decisiontable.core.DecisionTableLoader;
import org.megaknytes.decisiontable.core.EvaluationResult;
import org.megaknytes.decisiontable.core.LoadedDecisionTable;
import org.megaknytes.decisiontable.core.rules.value.ValueParserRegistry;
import org.megaknytes.decisiontable.ftc.discovery.DTClassDiscovery;
import org.megaknytes.decisiontable.ftc.discovery.DTFileDiscovery;
import org.megaknytes.decisiontable.ftc.xml.Ruleset;
import org.megaknytes.decisiontable.ftc.xml.SystemConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

public class DTProcessor {
    private static final DTProcessor INSTANCE = new DTProcessor();
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());

    private final Map<String, Device> availableDeviceDrivers = DTClassDiscovery.getDriverInstances();
    private final ValueParserRegistry valueParserRegistry = DTClassDiscovery.getValueParserRegistry();
    private final DecisionTableEvaluator evaluator = new DecisionTableEvaluator();

    private Map<String, Ruleset> enabledRulesets = new HashMap<>();
    private LoadedDecisionTable currentTable;
    private LoadedSystemConfiguration currentSystemConfiguration;
    private OpMode currentOpMode;

    public DTProcessor() {
    }

    @OnCreateEventLoop
    public static void scanForFiles(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled system configurations and rulesets...");

        try {
            File[] deviceXMLFiles = DTFileDiscovery.getDeviceXMLFiles(context);
            Map<String, SystemConfiguration> enabledSystemConfigurations = DTFileDiscovery.getEnabledSystemConfigurations(deviceXMLFiles);
            LOGGER.log(Level.INFO, "Enabled system configurations: " + enabledSystemConfigurations.keySet());
            INSTANCE.enabledRulesets = DTFileDiscovery.getEnabledRulesets(deviceXMLFiles, enabledSystemConfigurations);
            LOGGER.log(Level.INFO, "Enabled rulesets: " + INSTANCE.enabledRulesets.keySet());
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Error during XML parsing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @OpModeRegistrar
    public static void registerOpModes(AnnotatedOpModeManager opModeManager) {
        for (Map.Entry<String, Ruleset> entry : INSTANCE.enabledRulesets.entrySet()) {
            if (!OpModeMeta.nameIsLegalForOpMode(entry.getKey(), false)) {
                RobotLog.setGlobalErrorMsg("Decision Table with name '"+ entry.getKey() + "' skipped as name is incompatible.");
                continue;
            }

            Ruleset ruleset = entry.getValue();

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
                            INSTANCE.initializeRuleset(this, ruleset);
                        }

                        @Override
                        public void start() {
                            resetRuntime();
                        }

                        @Override
                        public void loop() {
                            INSTANCE.update();
                        }
                    });
        }
    }

    private void initializeRuleset(OpMode opMode, Ruleset ruleset) {
        reset();
        currentOpMode = opMode;

        try {
            FtcDeviceFactory deviceFactory = new FtcDeviceFactory(opMode);
            LoadedSystemConfiguration systemConfiguration = new SystemConfigurationLoader(availableDeviceDrivers, deviceFactory, valueParserRegistry).load(ruleset.getConfiguration().getFile());
            currentSystemConfiguration = systemConfiguration;
            currentTable = new DecisionTableLoader(valueParserRegistry).load(ruleset.getFile(), systemConfiguration);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to load ruleset '" + ruleset.getFile().getName() + "'", e);
            RobotLog.addGlobalWarningMessage("Failed to load ruleset '" + ruleset.getFile().getName() + "': " + e.getMessage());

            opMode.requestOpModeStop();
        }
    }

    private void update() {
        long startTime = System.nanoTime();

        if (currentTable == null) {
            return;
        }

        try {
            EvaluationResult result = evaluator.evaluate(currentTable, currentSystemConfiguration);
            reportResult(result);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while evaluating decision table", e);
            if (currentOpMode != null) {
                currentOpMode.telemetry.addData("Rule evaluation failed", e.getMessage());
            }
        }

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        if (currentOpMode != null) {
            currentOpMode.telemetry.addData("Evaluation Time (ms)", durationMillis);
        }
    }

    private void reportResult(EvaluationResult result) {
        if (currentOpMode == null) {
            return;
        }

        currentOpMode.telemetry.addData("Rules Matched", result.getMatchedRuleNames());

        if (!result.getConflicts().isEmpty()) {
            currentOpMode.telemetry.addData("Conflicts", describe(result.getConflicts()));
        }

        if (!result.getErrors().isEmpty()) {
            for (EvaluationResult.EvaluationError error : result.getErrors()) {
                LOGGER.log(Level.WARNING, error.toString(), error.getCause());
            }
            currentOpMode.telemetry.addData("Errors", describe(result.getErrors()));
        }
    }

    private static String describe(List<?> items) {
        return items.stream().map(Object::toString).collect(Collectors.joining("; "));
    }

    public static void reset() {
        INSTANCE.currentTable = null;
        INSTANCE.currentSystemConfiguration = null;
        INSTANCE.currentOpMode = null;
        LOGGER.log(Level.INFO, "DTProcessor has been reset");
    }

    public static DTProcessor getInstance() {
        return INSTANCE;
    }
}