package org.megaknytes.ftc.decisiontable.core.xml.values.types;

import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.registry.InternalVariableRegistry;
import org.megaknytes.ftc.decisiontable.core.xml.structure.configuration.InternalVariable;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InternalVariableValue<T> implements Value<T> {
    private final InternalVariableRegistry registry = InternalVariableRegistry.getInstance();
    private String groupName;
    private String variableName;

    public InternalVariableValue() {
    }

    @Override
    public T parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        Element variableElement = XMLHelperMethods.getFirstChildElement(parameterNode.getChildNodes());
        if (variableElement == null) {
            throw new ConfigurationException("Missing InternalVariable element");
        }

        Element groupElement = XMLHelperMethods.getFirstChildElement(variableElement.getChildNodes());
        if (groupElement != null) {
            groupName = groupElement.getNodeName();
        } else {
            throw new ConfigurationException("Group element missing in InternalVariable");
        }

        Element varElement = XMLHelperMethods.getFirstChildElement(groupElement.getChildNodes());
        if (varElement != null) {
            variableName = varElement.getNodeName();
        } else {
            throw new ConfigurationException("Variable element missing in Group");
        }

        return null;
    }

    @Override
    public void setValue(T value) {
        if (groupName == null || variableName == null) {
            throw new ConfigurationException("Group name or variable name not set");
        }

        InternalVariable<T> variable = registry.getVariable(groupName, variableName);
        variable.setValue(value);
    }

    @Override
    public T getValue() {
        if (groupName == null || variableName == null) {
            throw new ConfigurationException("Group name or variable name not set");
        }

        InternalVariable<T> variable = registry.getVariable(groupName, variableName);
        return variable.getValue();
    }

    @Override
    public Class<?> getType() {
        if (groupName != null && variableName != null) {
            InternalVariable<T> variable = registry.getVariable(groupName, variableName);
            return variable.getType();
        }
        return Object.class;
    }
}