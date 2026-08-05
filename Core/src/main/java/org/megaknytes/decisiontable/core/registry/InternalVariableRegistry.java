package org.megaknytes.decisiontable.core.registry;

import org.megaknytes.decisiontable.core.utils.exceptions.IllegalParameterException;

import java.util.HashMap;
import java.util.Map;

public class InternalVariableRegistry {

    private final Map<String, Map<String, InternalVariable<?>>> internalVariables = new HashMap<>();

    public InternalVariableRegistry() {
    }

    @SuppressWarnings("unchecked")
    public <T> InternalVariable<T> getVariable(String groupName, String variableName) {
        Map<String, InternalVariable<?>> group = getGroup(groupName);

        if (group == null) {
            throw new IllegalParameterException("Group not found: " + groupName);
        }

        InternalVariable<?> variable = group.get(variableName);

        if (variable == null) {
            throw new IllegalParameterException("Variable not found: " + variableName);
        }

        return (InternalVariable<T>) variable;
    }

    public Map<String, InternalVariable<?>> getGroup(String groupName) {
        return internalVariables.get(groupName);
    }

    public <T> void addVariable(String groupName, String variableName, String description, T value, Class<T> type) {
        addVariable(groupName, variableName, description, value, type, null);
    }

    public <T> void addVariable(String groupName, String variableName, String description, T value, Class<T> type, String stateTypeName) {
        Map<String, InternalVariable<?>> group = internalVariables.computeIfAbsent(groupName, k -> new HashMap<>());
        group.put(variableName, new InternalVariable<>(variableName, description, value, type, stateTypeName));
    }

    public Map<String, Map<String, InternalVariable<?>>> getAllGroups() {
        return internalVariables;
    }
}