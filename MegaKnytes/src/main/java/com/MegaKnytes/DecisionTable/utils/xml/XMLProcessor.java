package com.MegaKnytes.DecisionTable.utils.xml;

import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.MegaKnytes.DecisionTable.drivers.DTPDriver;

public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());

    public HashMap<String, HashMap<DTPDriver, HashMap<String, Object>>> processXMLDevices(NodeList deviceNodes, HashMap<String, Class<? extends DTPDriver>> driverClasses){
        HashMap<String, HashMap<DTPDriver, HashMap<String, Object>>> initializedDevices = new HashMap<>();

        for (int i = 0; i < deviceNodes.getLength(); i++) {
            Node deviceNode = deviceNodes.item(i);

            if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element deviceElement = (Element) deviceNode;
                String deviceName = deviceElement.getTagName();

                Node driverNode = deviceElement.getFirstChild();

                while (driverNode != null && driverNode.getNodeType() != Node.ELEMENT_NODE) {
                    driverNode = driverNode.getNextSibling();
                }

                if (driverNode != null) {
                    Element driverElement = (Element) driverNode;
                    String driverName = driverElement.getTagName();

                    try {
                        for (Class<? extends DTPDriver> driverClass : driverClasses.values()) {
                            if (driverClass.getSimpleName().equalsIgnoreCase(driverName)) {
                                HashMap<String, Object> deviceConfig = getConfigOptions(driverElement);
                                HashMap<DTPDriver, HashMap<String, Object>> compiledDevice = new HashMap<>();
                                DTPDriver driverInstance = driverClass.asSubclass(DTPDriver.class).getDeclaredConstructor().newInstance();
                                compiledDevice.put(driverInstance, deviceConfig);
                                initializedDevices.put(deviceName, compiledDevice);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return initializedDevices;
    }

    public List<Rule> processXMLRules(NodeList ruleNodes, HashMap<String, HashMap<DTPDriver, HashMap<String, Object>>> deviceDrivers) {
        List<Rule> rules = new ArrayList<>();

        for (int i = 0; i < ruleNodes.getLength(); i++) {
            Node ruleNode = ruleNodes.item(i);

            if (ruleNode.getNodeType() == Node.ELEMENT_NODE) {
                Element ruleElement = (Element) ruleNode;
                String description = ruleElement.getAttribute("description");

                NodeList conditionNodes = ruleElement.getElementsByTagName("Condition");
                List<Condition> conditions = new ArrayList<>();
                for (int j = 0; j < conditionNodes.getLength(); j++) {
                    Element conditionElement = (Element) conditionNodes.item(j);
                    String device = conditionElement.getElementsByTagName("Device").item(0).getTextContent();
                    String property = conditionElement.getElementsByTagName("Property").item(0).getTextContent();
                    String comparison = conditionElement.getElementsByTagName("Comparison").item(0).getTextContent();
                    String value = conditionElement.getElementsByTagName("Value").item(0).getTextContent();
                    conditions.add(new Condition(device, property, comparison, value));
                }

                NodeList actionNodes = ruleElement.getElementsByTagName("Action");
                List<Action> actions = new ArrayList<>();
                for (int j = 0; j < actionNodes.getLength(); j++) {
                    Element actionElement = (Element) actionNodes.item(j);
                    String device = actionElement.getElementsByTagName("Device").item(0).getTextContent();
                    String property = actionElement.getElementsByTagName("Property").item(0).getTextContent();
                    String value = actionElement.getElementsByTagName("Value").item(0).getTextContent();
                    actions.add(new Action(device, property, value));
                }

                rules.add(new Rule(description, conditions, actions));
            }
        }
        return rules;
    }

    private HashMap<String, Object> getConfigOptions(Element element) {
        HashMap<String, Object> configOptions = new HashMap<>();
        NodeList options = element.getChildNodes();

        for (int j = 0; j < options.getLength(); j++) {
            Node optionNode = options.item(j);

            if (optionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element optionElement = (Element) optionNode;
                String optionName = optionElement.getTagName();
                String optionValue = optionElement.getTextContent().trim();
                configOptions.put(optionName, optionValue);
            }
        }
        return configOptions;
    }
}