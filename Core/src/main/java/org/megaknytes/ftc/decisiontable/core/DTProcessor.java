package org.megaknytes.ftc.decisiontable.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.firstinspires.ftc.ftccommon.external.OnCreate;
import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.ftccommon.internal.AnnotatedHooksClassFilter;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.megaknytes.ftc.decisiontable.core.utils.discovery.DTClassDiscovery;
import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.discovery.DTFileDiscovery;
import org.megaknytes.ftc.decisiontable.core.xml.ParameterRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.RulesetProcessor;
import org.megaknytes.ftc.decisiontable.core.xml.SystemConfigurationProcessor;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Action;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Condition;
import org.megaknytes.ftc.decisiontable.core.xml.structure.Ruleset;
import org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset.Rule;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.robot.Robot;

import org.megaknytes.ftc.decisiontable.core.xml.structure.SystemConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    private Map<String, SystemConfiguration> enabledSystemConfigurations = new HashMap<>();
    private Map<String, Ruleset> enabledRulesets = new HashMap<>();
    private final Map<String, DTDevice> availableDeviceDrivers = DTClassDiscovery.getDriverInstances();
    private final ParameterRegistry parameterRegistry = ParameterRegistry.getInstance();
    private FtcRobotControllerService robotControllerService;

    public DTProcessor() {}

    @OnCreateEventLoop
    public static void onCreateEventLoop(Context context, FtcEventLoop eventLoop) {
        LOGGER.log(Level.INFO, "Beginning to scan for enabled system configurations and rulesets...");
        try {
            context.bindService(new Intent(context, FtcRobotControllerService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    INSTANCE.robotControllerService = ((FtcRobotControllerService.FtcRobotControllerBinder) service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            }, Context.BIND_AUTO_CREATE);

            while (INSTANCE.robotControllerService.getRobot() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Service connection interrupted: " + e.getMessage());
                }
            }

            try {
                INSTANCE.enabledSystemConfigurations = DTFileDiscovery.getEnabledSystemConfigurations(context);
                LOGGER.log(Level.INFO, "Enabled system configurations: " + INSTANCE.enabledSystemConfigurations.keySet());
                INSTANCE.enabledRulesets = DTFileDiscovery.getEnabledRulesets(context, INSTANCE.robotControllerService.getRobot().eventLoopManager, INSTANCE.enabledSystemConfigurations);
                LOGGER.log(Level.INFO, "Enabled rulesets: " + INSTANCE.enabledRulesets.keySet());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading rulesets: " + e.getMessage());
            }
        } catch (Exception e) {
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
                            .setName(tableName + " [DT]")
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
        Map<String, DTDevice> deviceInstances = new HashMap<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document rulesetDocument = builder.parse(ruleset.getFile());
            Document systemConfigDocument = builder.parse(ruleset.getConfiguration().getFile());

            rulesetDocument.getDocumentElement().normalize();
            systemConfigDocument.getDocumentElement().normalize();

            NodeList deviceElements = systemConfigDocument.getElementsByTagName("Devices");
            for (int i = 0; i < deviceElements.getLength(); i++) {
                deviceInstances.putAll(SystemConfigurationProcessor.processDevices(deviceElements.item(i).getChildNodes(), opMode, availableDeviceDrivers, parameterRegistry));
            }

            NodeList rulesElements = rulesetDocument.getElementsByTagName("Rules");
            for (int i = 0; i < rulesElements.getLength(); i++) {
                loadedRules.addAll(RulesetProcessor.processRules(rulesElements.item(i).getChildNodes(), deviceInstances, parameterRegistry));
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

    public static DTProcessor getInstance() {
        return INSTANCE;
    }

    public void restartRobot(){
        INSTANCE.robotControllerService.shutdownRobot();
    }
}