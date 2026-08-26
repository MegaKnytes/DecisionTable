package org.megaknytes.decisiontable.core.parse;

import org.megaknytes.decisiontable.core.SystemConfiguration;
import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.device.DeviceFactory;
import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.rule.value.type.State;
import org.megaknytes.decisiontable.core.rule.value.ValueParserRegistry;
import org.megaknytes.decisiontable.core.rule.value.Value;
import org.megaknytes.decisiontable.core.rule.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.device.InitializedDevice;
import org.megaknytes.decisiontable.core.utils.exception.XmlParseException;
import org.megaknytes.decisiontable.core.utils.xml.XmlElements;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;
import org.megaknytes.decisiontable.core.utils.exception.DriverNotFoundException;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;
import org.megaknytes.decisiontable.core.rule.value.type.registry.StateRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.registry.InternalVariableRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Responsible for loading a file containing System Configuration XML into a System Configuration object.
 */
public class SystemConfigurationParser {
    private final Map<String, Device> availableDrivers;
    private final DeviceFactory deviceFactory;
    private final ValueParserRegistry valueParserRegistry;
    private final ValueParser valueParser;

    public SystemConfigurationParser(Map<String, Device> availableDrivers, DeviceFactory deviceFactory, ValueParserRegistry valueParserRegistry) {
        this.availableDrivers = availableDrivers;
        this.deviceFactory = deviceFactory;
        this.valueParserRegistry = valueParserRegistry;
        this.valueParser = new ValueParser(valueParserRegistry);
    }

    /**
     * Given an XML file containing a System Configuration, attempt to create a System Configuration object
     *
     * @param file the File that contains the System Configuration XML
     * @return a System Configuration object matching the given XML file
     */
    public SystemConfiguration load(File file) {
        Document document;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new XmlParseException("Failed to parse " + file.getName() + ": " + e.getMessage());
        }

        Element root = document.getDocumentElement();
        String name = root.getAttribute("name");

        ParameterRegistry parameterRegistry = new ParameterRegistry();
        InternalVariableRegistry internalVariableRegistry = new InternalVariableRegistry();
        StateRegistry stateRegistry = new StateRegistry();
        Map<String, Device> devices = new LinkedHashMap<>();

        SystemConfiguration configuration = new SystemConfiguration(name, devices, parameterRegistry, internalVariableRegistry, stateRegistry);

        registerStates(root, stateRegistry);
        registerVariables(root, internalVariableRegistry, stateRegistry);
        registerDevices(root, devices, parameterRegistry, configuration);

        for (Device device : devices.values()) {
            if (device instanceof InitializedDevice) {
                ((InitializedDevice) device).initialize();
            }
        }

        return configuration;
    }

    /**
     * Register every declared state
     *
     * @param root the document root
     * @param registry the state registry to add declared state types to
     */
    private void registerStates(Element root, StateRegistry registry) {
        for (Element section : XmlElements.childElementsNamed(root, "States")) {
            for (Element stateElement : XmlElements.childElementsNamed(section, "State")) {
                String typeName = stateElement.getAttribute("name");

                if (registry.hasType(typeName)) {
                    throw new ConfigurationException("Duplicate state type: " + typeName);
                } else if (valueParserRegistry.getRegisteredParsers().values().stream().anyMatch(parser -> parser.getType().getSimpleName().equals(typeName))) {
                    throw new ConfigurationException("State type '" + typeName + "' conflicts with a registered type");
                }

                Set<String> values = new LinkedHashSet<>();

                for (Element valueElement : XmlElements.childElementsNamed(stateElement, "Value")) {
                    String valueName = valueElement.getAttribute("name");

                    if (!values.add(valueName)) {
                        throw new ConfigurationException("Duplicate value '" + valueName + "' in state '" + typeName + "'");
                    }
                }

                registry.addType(typeName, values);
            }
        }
    }

    /**
     * Register each declared internal variable group
     *
     * @param root the document root
     * @param registry the registry to add internal variables to
     * @param stateRegistry the registry used to get declared state types
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerVariables(Element root, InternalVariableRegistry registry, StateRegistry stateRegistry) {
        for (Element section : XmlElements.childElementsNamed(root, "InternalVariables")) {
            for (Element child : XmlElements.childElements(section)) {
                String groupName = child.getAttribute("name");

                for (Element variableElement : XmlElements.childElementsNamed(child, "Variable")) {
                    String variableName = variableElement.getAttribute("name");
                    String typeName = variableElement.getAttribute("type");
                    String literal = variableElement.getAttribute("value");
                    String description = XmlElements.optionalAttribute(variableElement, "description");

                    if (registry.getGroup(groupName) != null && registry.getGroup(groupName).containsKey(variableName)) {
                        throw new ConfigurationException("Duplicate internal variable: " + groupName + "." + variableName);
                    }

                    if (stateRegistry.hasType(typeName)) {
                        State parsedValue = stateRegistry.parse(typeName, literal);
                        registry.addStateVariable(groupName, variableName, description, parsedValue, typeName);
                        continue;
                    }

                    Class<?> type = valueParserRegistry.getRegisteredParsers().values().stream()
                            .filter(parser -> parser.getType().getSimpleName().equals(typeName))
                            .map(ValueTypeParser::getType)
                            .findFirst()
                            .orElse(null);

                    if (type == null) {
                        throw new ConfigurationException("Unknown internal variable type '" + typeName + "' (expected one of " + valueParserRegistry.getRegisteredParsers().keySet() + ")");
                    }

                    Object parsedValue = valueParserRegistry.getParser((Class) type).parse(literal);
                    registry.addVariable(groupName, variableName, description, parsedValue, (Class) type);
                }
            }
        }
    }


    /**
     * Initialize and register each device, then bind parameter values.
     *
     * @param root the document root
     * @param devices the map of instantiated devices to device names
     * @param parameterRegistry the registry with each device's parameters
     * @param configuration the system configuration
     */
    private void registerDevices(Element root, Map<String, Device> devices, ParameterRegistry parameterRegistry, SystemConfiguration configuration) {
        for (Element section : XmlElements.childElementsNamed(root, "Devices")) {
            for (Element deviceElement : XmlElements.childElementsNamed(section, "Device")) {
                String driverName = deviceElement.getAttribute("driver");
                String deviceName = deviceElement.getAttribute("name");

                if (devices.containsKey(deviceName)) {
                    throw new ConfigurationException("Duplicate device name: " + deviceName);
                }

                Device driverTemplate = availableDrivers.get(driverName);

                if (driverTemplate == null) {
                    throw new DriverNotFoundException("Driver with name " + driverName + " not found");
                }

                Device deviceInstance;

                try {
                    deviceInstance = deviceFactory.createAndBind(driverTemplate.getClass(), parameterRegistry);
                } catch (ReflectiveOperationException e) {
                    throw new ConfigurationException("Failed to instantiate device class for " + deviceName + ": " + e.getMessage());
                }

                deviceInstance.registerParameters(parameterRegistry);
                devices.put(deviceName, deviceInstance);

                for (Element parameterElement : XmlElements.childElementsNamed(deviceElement, "Parameter")) {
                    Parameter<?> parameter = parameterRegistry.getParameter(deviceInstance, parameterElement.getAttribute("name"));
                    bindParameter(parameter, parameterElement, configuration);
                }
            }
        }
    }

    /**
     * Apply the value to the parameter, then bind any sub-parameters
     *
     * @param parameter the parameter to bind
     * @param parameterElement the Parameter XML element
     * @param configuration the system configuration being built
     */
    private void bindParameter(Parameter<?> parameter, Element parameterElement, SystemConfiguration configuration) {
        if (parameter.getType() != null) {
            setParameterValue(parameter, parameterElement, configuration);
        }

        for (Element subElement : XmlElements.childElementsNamed(parameterElement, "Parameter")) {
            Parameter<?> subParameter = parameter.getSubParameter(subElement.getAttribute("name"));
            bindParameter(subParameter, subElement, configuration);
        }
    }

    /**
     * Set a parameters value from its literal or ref attribute
     *
     * @param parameter the parameter to set
     * @param parameterElement the XML element carrying the value or ref
     * @param configuration the system configuration being built
     */
    private void setParameterValue(Parameter<?> parameter, Element parameterElement, SystemConfiguration configuration) {
        String value = XmlElements.optionalAttribute(parameterElement, "value");
        String ref = XmlElements.optionalAttribute(parameterElement, "ref");
        String errorContext = "Device Parameter \"" + parameterElement.getAttribute("name") + "\"";

        Object resolved = getValue(parameter.getType(), value, parameterElement, ref, errorContext, configuration);
        parameter.setValue(resolved);
    }

    /**
     * Get a literal or reference value with an expectedType given its XML
     *
     * @param expectedType the type the resolved value should match
     * @param value the value, or null if a reference is used instead
     * @param element the XML with the value or ref attributes for use with custom XMLValueTypeParsers
     * @param ref the reference attribute, or null if a literal is used instead
     * @param errorContext a description of the current parse location for error messages
     * @param configuration the system configuration
     * @return the resolved value with type expectedType
     */
    private <T> T getValue(Class<T> expectedType, String value, Element element, String ref, String errorContext, SystemConfiguration configuration) {
        Value<T> source = valueParser.parse(value, element, ref, expectedType, errorContext);

        if (!source.getReferenceAddress()) {
            return source.getLiteral();
        }

        Object current = configuration.getReader(source.getReference()).get();

        try {
            return expectedType.cast(current);
        } catch (ClassCastException e) {
            throw new TypeMismatchException(errorContext + "'s reference \"" + source.getReference() + "\" ia a " + (current == null ? "null value" : current.getClass().getSimpleName()) + ", should be a " + expectedType.getSimpleName());
        }
    }
}