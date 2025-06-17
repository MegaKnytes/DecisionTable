package org.megaknytes.ftc.decisiontable.core.xml;

import org.megaknytes.ftc.decisiontable.core.drivers.DTDevice;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.IllegalParameterException;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.structure.parameters.ParameterGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ParameterRegistry {

    private static final ParameterRegistry INSTANCE = new ParameterRegistry();

    private final Map<DTDevice, Map<String, ParameterGroup>> deviceGroups = new HashMap<>();

    private ParameterRegistry() {
    }

    public static void reset() {
        INSTANCE.deviceGroups.clear();
    }

    public static ParameterRegistry getInstance() {
        return INSTANCE;
    }

    public Parameter<?> getParameter(DTDevice device, String groupName, String parameterName) {
        ParameterGroup group = getGroup(device, groupName);
        if (group == null) {
            throw new IllegalParameterException("Group not found: " + groupName);
        }
        Parameter<?> parameter = group.getParameter(parameterName);
        if (parameter == null) {
            throw new IllegalParameterException("Parameter not found: " + parameterName);
        }
        return parameter;
    }

    public ParameterGroup getGroup(DTDevice device, String groupName) {
        Map<String, ParameterGroup> groups = deviceGroups.get(device);
        if (groups == null) {
            return null;
        }
        return groups.get(groupName);
    }

    public ParameterGroupBuilder createParameterGroup(DTDevice device, String groupName) {
        Map<String, ParameterGroup> groups = deviceGroups.computeIfAbsent(device, k -> new HashMap<>());
        if (groups.containsKey(groupName)) {
            return new ParameterGroupBuilder(groups.get(groupName));
        } else {
            ParameterGroup group = new ParameterGroup(groupName);
            groups.put(groupName, group);
            return new ParameterGroupBuilder(group);
        }
    }

    public static class ParameterGroupBuilder {
        private final ParameterGroup group;

        public ParameterGroupBuilder(ParameterGroup group) {
            this.group = group;
        }

        public <T> ParameterGroupBuilder addParameter(String name, Class<T> type, Supplier<T> getter) {
            group.addParameter(name, type, getter);
            return this;
        }

        public <T> ParameterGroupBuilder addParameter(String name, Class<T> type, Supplier<T> getter, Consumer<T> listener) {
            group.addParameter(name, type, getter, listener);
            return this;
        }
    }
}