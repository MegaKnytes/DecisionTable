package org.megaknytes.decisiontable.core.config;

import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.rules.value.DeclaredStateValue;
import org.megaknytes.decisiontable.core.rules.value.ValueParserRegistry;
import org.megaknytes.decisiontable.core.rules.value.ValueSource;
import org.megaknytes.decisiontable.core.rules.value.ValueSourceParser;
import org.megaknytes.decisiontable.core.utils.xml.XmlDocumentLoader;
import org.megaknytes.decisiontable.core.utils.xml.XmlElements;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;
import org.megaknytes.decisiontable.core.registry.StateRegistry;
import org.megaknytes.decisiontable.core.registry.InternalVariableRegistry;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.registry.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SystemConfigurationLoader {
    private static final Map<String, Class<?>> PRIMITIVE_VARIABLE_TYPES = buildPrimitiveVariableTypes();

    private final Map<String, Device> availableDrivers;

    private final DeviceFactory deviceFactory;
    private final ValueParserRegistry valueParserRegistry;
    private final ValueSourceParser valueSourceParser;
    private final XmlDocumentLoader xmlDocumentLoader;

    public SystemConfigurationLoader(Map<String, Device> availableDrivers, DeviceFactory deviceFactory, ValueParserRegistry valueParserRegistry) {
        this.availableDrivers = availableDrivers;
        this.deviceFactory = deviceFactory;
        this.valueParserRegistry = valueParserRegistry;
        this.valueSourceParser = new ValueSourceParser(valueParserRegistry);
        this.xmlDocumentLoader = new XmlDocumentLoader();
    }

    public LoadedSystemConfiguration load(File file) {
        return load(xmlDocumentLoader.parse(file));
    }

    public LoadedSystemConfiguration load(Document document) {
        Element root = document.getDocumentElement();
        String name = root.getAttribute("name");
        boolean enabled = parseXsdBoolean(root.getAttribute("enabled"));

        ParameterRegistry parameterRegistry = new ParameterRegistry();
        InternalVariableRegistry internalVariableRegistry = new InternalVariableRegistry();
        StateRegistry stateRegistry = new StateRegistry();
        Map<String, Device> devices = new LinkedHashMap<>();

        LoadedSystemConfiguration configuration = new LoadedSystemConfiguration(name, enabled, devices, parameterRegistry, internalVariableRegistry, stateRegistry);

        bindStates(root, stateRegistry);
        bindVariables(root, internalVariableRegistry, stateRegistry);
        bindDevices(root, devices, parameterRegistry, configuration);

        return configuration;
    }

    private void bindStates(Element root, StateRegistry registry) {
        for (Element section : XmlElements.childElementsNamed(root, "States")) {
            for (Element stateElement : XmlElements.childElementsNamed(section, "State")) {
                String typeName = stateElement.getAttribute("name");
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

    private void bindVariables(Element root, InternalVariableRegistry registry, StateRegistry stateRegistry) {
        for (Element section : XmlElements.childElementsNamed(root, "InternalVariables")) {
            for (Element child : XmlElements.childElements(section)) {
                if (child.getTagName().equals("VariableGroup")) {
                    String groupName = child.getAttribute("name");

                    for (Element variableElement : XmlElements.childElementsNamed(child, "Variable")) {
                        bindVariable(variableElement, groupName, registry, stateRegistry);
                    }
                } else {
                    bindVariable(child, child.getAttribute("group"), registry, stateRegistry);
                }
            }
        }
    }

    private void bindVariable(Element variableElement, String group, InternalVariableRegistry registry, StateRegistry stateRegistry) {
        String variableName = variableElement.getAttribute("name");
        String typeName = variableElement.getAttribute("type");
        String literal = variableElement.getAttribute("value");
        String description = XmlElements.optionalAttribute(variableElement, "description");

        if (registry.getGroup(group) != null && registry.getGroup(group).containsKey(variableName)) {
            throw new ConfigurationException("Duplicate internal variable: " + group + "." + variableName);
        }

        if (stateRegistry.hasType(typeName)) {
            DeclaredStateValue parsedValue = stateRegistry.parse(typeName, literal);
            registry.addVariable(group, variableName, description, parsedValue, DeclaredStateValue.class, typeName);
            return;
        }

        Class<?> type = resolveVariableType(typeName);
        Object parsedValue = valueParserRegistry.getParser(castParserType(type)).parse(literal);
        addVariable(registry, group, variableName, description, type, parsedValue);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castParserType(Class<?> type) {
        return (Class<T>) type;
    }

    @SuppressWarnings("unchecked")
    private static <T> void addVariable(InternalVariableRegistry registry, String group, String name, String description, Class<?> type, Object value) {
        registry.addVariable(group, name, description, (T) value, (Class<T>) type);
    }

    private static Class<?> resolveVariableType(String typeName) {
        Class<?> type = PRIMITIVE_VARIABLE_TYPES.get(typeName);

        if (type == null) {
            throw new ConfigurationException("Unknown internal variable type '" + typeName + "' (expected one of " + PRIMITIVE_VARIABLE_TYPES.keySet() + ")");
        }

        return type;
    }

    private void bindDevices(Element root, Map<String, Device> devices, ParameterRegistry parameterRegistry, LoadedSystemConfiguration configuration) {
        for (Element section : XmlElements.childElementsNamed(root, "Devices")) {
            for (Element deviceElement : XmlElements.childElementsNamed(section, "Device")) {
                String driverName = deviceElement.getAttribute("driver");
                String deviceName = deviceElement.getAttribute("name");

                if (devices.containsKey(deviceName)) {
                    throw new ConfigurationException("Duplicate device name: " + deviceName);
                }

                Device driverTemplate = availableDrivers.get(driverName);

                if (driverTemplate == null) {
                    throw new DriverNotFoundException("Driver with name " + driverName + " not found, has it been enabled?");
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

    private void bindParameter(Parameter<?> parameter, Element parameterElement, LoadedSystemConfiguration configuration) {
        if (parameter.getType() != null) {
            applyParameterValue(parameter, parameterElement, configuration);
        }

        for (Element subElement : XmlElements.childElementsNamed(parameterElement, "Parameter")) {
            Parameter<?> subParameter = parameter.getSubParameter(subElement.getAttribute("name"));
            bindParameter(subParameter, subElement, configuration);
        }
    }

    private void applyParameterValue(Parameter<?> parameter, Element parameterElement, LoadedSystemConfiguration configuration) {
        String value = XmlElements.optionalAttribute(parameterElement, "value");
        String ref = XmlElements.optionalAttribute(parameterElement, "ref");
        String context = "Device Parameter \"" + parameterElement.getAttribute("name") + "\"";

        Object resolved = resolveValue(parameter.getType(), value, ref, context, configuration);
        parameter.setValue(resolved);
    }

    private <T> T resolveValue(Class<T> expectedType, String value, String ref, String context, LoadedSystemConfiguration configuration) {
        ValueSource<T> source = valueSourceParser.parse(value, ref, expectedType, context);

        if (!source.isReference()) {
            return source.getLiteral();
        }

        Object current = configuration.resolveCurrentValue(source.getReference());

        try {
            return expectedType.cast(current);
        } catch (ClassCastException e) {
            throw new TypeMismatchException(context + "'s ref \"" + source.getReference() + "\" resolved to a " + (current == null ? "null value" : current.getClass().getSimpleName()) + ", expected " + expectedType.getSimpleName());
        }
    }

    private static boolean parseXsdBoolean(String literal) {
        return "true".equals(literal) || "1".equals(literal);
    }

    private static Map<String, Class<?>> buildPrimitiveVariableTypes() {
        Map<String, Class<?>> types = new HashMap<>();
        types.put("Boolean", Boolean.class);
        types.put("Double", Double.class);
        types.put("Float", Float.class);
        types.put("Integer", Integer.class);
        types.put("String", String.class);
        return Collections.unmodifiableMap(types);
    }
}