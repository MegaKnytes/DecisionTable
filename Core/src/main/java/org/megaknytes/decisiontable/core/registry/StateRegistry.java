package org.megaknytes.decisiontable.core.registry;

import org.megaknytes.decisiontable.core.rules.value.DeclaredStateValue;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class StateRegistry {
    private final Map<String, Set<String>> types = new LinkedHashMap<>();

    public void addType(String typeName, Set<String> values) {
        if (types.containsKey(typeName)) {
            throw new ConfigurationException("Duplicate state: " + typeName);
        }

        types.put(typeName, Collections.unmodifiableSet(new LinkedHashSet<>(values)));
    }

    public boolean hasType(String typeName) {
        return types.containsKey(typeName);
    }

    public Set<String> getValues(String typeName) {
        Set<String> values = types.get(typeName);

        if (values == null) {
            throw new ConfigurationException("Unknown state: " + typeName);
        }

        return values;
    }

    public DeclaredStateValue parse(String typeName, String literalValue) {
        Set<String> values = getValues(typeName);

        if (!values.contains(literalValue)) {
            throw new ConfigurationException("'" + literalValue + "' is not a legal value for state '" + typeName + "' (expected one of " + values + ")");
        }

        return new DeclaredStateValue(typeName, literalValue);
    }
}
