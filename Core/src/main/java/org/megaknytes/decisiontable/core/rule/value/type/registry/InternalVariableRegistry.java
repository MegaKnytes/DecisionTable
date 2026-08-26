package org.megaknytes.decisiontable.core.rule.value.type.registry;

import org.megaknytes.decisiontable.core.rule.value.type.InternalVariable;
import org.megaknytes.decisiontable.core.rule.value.type.State;
import org.megaknytes.decisiontable.core.utils.exception.IllegalParameterException;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry of internal variables that can be referenced in decision tables
 */
public class InternalVariableRegistry {

    private final Map<String, Map<String, InternalVariable<?>>> internalVariables = new HashMap<>();

    public InternalVariableRegistry() {
    }

    /**
     * @param groupName    the group that the variable belongs to
     * @param variableName the name of the variable to get
     * @return the InternalVariable registered under the given group and name
     */
    @SuppressWarnings("unchecked")
    public <T> InternalVariable<T> getVariable(String groupName, String variableName) {
        Map<String, InternalVariable<?>> group = getGroup(groupName);

        if (group == null) {
            throw new IllegalParameterException("Variable group not found: " + groupName);
        }

        InternalVariable<?> variable = group.get(variableName);

        if (variable == null) {
            throw new IllegalParameterException("Variable with name '" + variableName + "' not found in group '" + groupName + "'");
        }

        // typecast to the expected type
        return (InternalVariable<T>) variable;
    }

    public Map<String, InternalVariable<?>> getGroup(String groupName) {
        return internalVariables.get(groupName);
    }

    public <T> void addVariable(String groupName, String variableName, String description, T value, Class<T> type) {
        Map<String, InternalVariable<?>> group = internalVariables.computeIfAbsent(groupName, k -> new HashMap<>());
        group.put(variableName, new InternalVariable<>(variableName, description, value, type, null));
    }

    public void addStateVariable(String groupName, String variableName, String description, State value, String stateTypeName) {
        Map<String, InternalVariable<?>> group = internalVariables.computeIfAbsent(groupName, k -> new HashMap<>());
        group.put(variableName, new InternalVariable<>(variableName, description, value, State.class, stateTypeName));
    }
}