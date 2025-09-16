package org.megaknytes.ftc.decisiontable.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.discovery.DTClassDiscovery;
import org.megaknytes.ftc.decisiontable.core.utils.discovery.DTFileDiscovery;
import org.megaknytes.ftc.decisiontable.core.xml.processing.RulesetProcessor;
import org.megaknytes.ftc.decisiontable.core.xml.processing.SystemConfigurationProcessor;
import org.megaknytes.ftc.decisiontable.core.xml.registry.InternalVariableRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.registry.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Ruleset;
import org.megaknytes.ftc.decisiontable.core.xml.structure.SystemConfiguration;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Rule;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DTProcessor {
    private static final DTProcessor INSTANCE = new DTProcessor();
    private static final Logger LOGGER = Logger.getLogger(DTProcessor.class.getName());
    private final List<Rule> loadedRules = new ArrayList<>();
    private final List<Action> pendingActions = new ArrayList<>();
    private final Map<String, DTDevice> availableDeviceDrivers = DTClassDiscovery.getDriverInstances();
    private Map<String, Ruleset> enabledRulesets = new HashMap<>();
    private Robot robot;

    public DTProcessor() {
    }

    @OnCreateEventLoop
    public static void bindRobot(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Attempting to bind to the FtcRobotControllerService...");
        context.bindService(new Intent(context, FtcRobotControllerService.class), new ServiceConnection() {
            private static final int MAX_RETRY_COUNT = 75;
            private final android.os.Handler handler = new android.os.Handler();
            private IBinder service;
            private int retryCount = 0;

            private final Runnable robotChecker = new Runnable() {
                @Override
                public void run() {
                    FtcRobotControllerService robotControllerService = ((FtcRobotControllerService.FtcRobotControllerBinder) service).getService();

                    if (robotControllerService.getRobot() != null) {
                        INSTANCE.robot = robotControllerService.getRobot();
                        LOGGER.log(Level.INFO, "Robot successfully bound");
                    } else if (retryCount < MAX_RETRY_COUNT) {
                        retryCount++;
                        handler.postDelayed(this, 100);
                    } else {
                        LOGGER.log(Level.WARNING, "Timed out waiting for robot to initialize");
                    }
                }
            };

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                this.service = service;
                handler.post(robotChecker);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                handler.removeCallbacks(robotChecker);
                INSTANCE.robot = null;
            }
        }, Context.BIND_AUTO_CREATE);
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
            String tableName = entry.getKey();
            Ruleset ruleset = entry.getValue();
            OpModeMeta.Flavor opmodeFlavor = ruleset.getFlavor();
            String transitionTarget = ruleset.getTransitionTarget();

            opModeManager.register(
                    new OpModeMeta.Builder()
                            .setName(tableName)
                            .setFlavor(opmodeFlavor)
                            .setGroup("DecisionTable")
                            .setSource(OpModeMeta.Source.EXTERNAL_LIBRARY)
                            .setTransitionTarget(transitionTarget)
                            .build(),
                    new OpMode() {
                        @Override
                        public void init() {
                            INSTANCE.initializeRuleset(this, ruleset);
                            telemetry.addData("Status: ", tableName + " has been initialized");
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
        Map<String, DTDevice> deviceInstances = new HashMap<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document rulesetDocument = builder.parse(ruleset.getFile());
            Document systemConfigDocument = builder.parse(ruleset.getConfiguration().getFile());

            rulesetDocument.getDocumentElement().normalize();
            systemConfigDocument.getDocumentElement().normalize();

            NodeList deviceElements = systemConfigDocument.getElementsByTagName("Devices");
            for (int i = 0; i < deviceElements.getLength(); i++) {
                deviceInstances.putAll(SystemConfigurationProcessor.processDevices(deviceElements.item(i).getChildNodes(), opMode, availableDeviceDrivers));
            }

            NodeList internalVariablesElements = systemConfigDocument.getElementsByTagName("InternalVariables");
            for (int i = 0; i < internalVariablesElements.getLength(); i++) {
                SystemConfigurationProcessor.processInternalVariables(internalVariablesElements.item(i).getChildNodes(), DTClassDiscovery.getValueParserClasses());
            }

            NodeList rulesElements = rulesetDocument.getElementsByTagName("Rules");
            for (int i = 0; i < rulesElements.getLength(); i++) {
                loadedRules.addAll(RulesetProcessor.processRules(rulesElements.item(i).getChildNodes(), deviceInstances));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file", e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Error parsing XML configuration: " + e.getMessage(), e);
        }
    }

    private void update() {
        try {
            pendingActions.clear();

            for (Rule rule : loadedRules) {
                boolean allConditionsMet = true;

                for (Condition condition : rule.getConditions()) {
                    if (!condition.evaluate()) {
                        allConditionsMet = false;
                        break;
                    }
                }

                if (allConditionsMet) {
                    pendingActions.addAll(rule.getActions());
                }
            }

            for (Action action : pendingActions) {
                try {
                    action.execute();
                } catch (Exception e) {
                    throw new RuntimeException("Error executing action: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during update cycle: " + e.getMessage(), e);
        }
    }

    public static void reset() {
        INSTANCE.loadedRules.clear();
        INSTANCE.pendingActions.clear();
        ParameterRegistry.reset();
        InternalVariableRegistry.reset();
        LOGGER.log(Level.INFO, "DTProcessor has been reset");
    }

    public static DTProcessor getInstance() {
        return INSTANCE;
    }

    public static Robot getRobot() {
        return INSTANCE.robot;
    }
}