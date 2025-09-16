package org.megaknytes.ftc.decisiontable.core.xml.registry;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.configuration.InternalVariable;

import java.util.HashMap;
import java.util.Map;

public class InternalVariableRegistry {

    private static final InternalVariableRegistry INSTANCE = new InternalVariableRegistry();

    private final Map<String, Map<String, InternalVariable<?>>> internalVariables = new HashMap<>();

    private InternalVariableRegistry() {
    }

    public static void reset() {
        INSTANCE.internalVariables.clear();
    }

    public static InternalVariableRegistry getInstance() {
        return INSTANCE;
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
        Map<String, InternalVariable<?>> group = internalVariables.computeIfAbsent(groupName, k -> new HashMap<>());
        group.put(variableName, new InternalVariable<>(variableName, description, value, type));
    }

    public Map<String, Map<String, InternalVariable<?>>> getAllGroups() {
        return internalVariables;
    }
}