package org.megaknytes.decisiontable.core.rule.value.type.registry;

import org.megaknytes.decisiontable.core.rule.value.type.State;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A registry of declared states their values
 */
public final class StateRegistry {
    private final Map<String, Set<String>> types = new LinkedHashMap<>();

    public void addType(String typeName, Set<String> values) {
        if (types.containsKey(typeName)) {
            throw new ConfigurationException("Attempted to add state that exists: " + typeName);
        }

        types.put(typeName, Collections.unmodifiableSet(new LinkedHashSet<>(values)));
    }

    public boolean hasType(String typeName) {
        return types.containsKey(typeName);
    }


    public Set<String> getValues(String typeName) {
        Set<String> values = types.get(typeName);

        if (values == null) {
            throw new ConfigurationException("Unknown State: " + typeName);
        }

        return values;
    }

    /**
     * Parses a literal against a states allowed values
     *
     * @param typeName the declared state the literal belongs to
     * @param literal the value to parse
     * @return the parsed state
     */
    public State parse(String typeName, String literal) {
        Set<String> values = getValues(typeName);

        if (!values.contains(literal)) {
            throw new ConfigurationException("'" + literal + "' is not a value for state '" + typeName + "' (expected one of " + values + ")");
        }

        return new State(typeName, literal);
    }
}