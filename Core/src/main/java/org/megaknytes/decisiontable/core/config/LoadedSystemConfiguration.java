package org.megaknytes.decisiontable.core.config;

import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.rules.address.AddressScheme;
import org.megaknytes.decisiontable.core.rules.address.TargetAddress;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.decisiontable.core.utils.exceptions.DriverNotFoundException;
import org.megaknytes.decisiontable.core.registry.StateRegistry;
import org.megaknytes.decisiontable.core.registry.InternalVariableRegistry;
import org.megaknytes.decisiontable.core.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.registry.InternalVariable;
import org.megaknytes.decisiontable.core.registry.Parameter;
import org.megaknytes.decisiontable.core.rules.value.DeclaredStateValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LoadedSystemConfiguration {
    private final String name;
    private final boolean enabled;

    private final Map<String, Device> devices;

    private final ParameterRegistry parameterRegistry;
    private final InternalVariableRegistry internalVariableRegistry;
    private final StateRegistry stateRegistry;

    private final Map<TargetAddress, PreviousValueTracker> previousValueTrackers = new HashMap<>();

    public LoadedSystemConfiguration(String name, boolean enabled, Map<String, Device> devices, ParameterRegistry parameterRegistry, InternalVariableRegistry internalVariableRegistry, StateRegistry stateRegistry) {
        this.name = name;
        this.enabled = enabled;
        this.devices = Collections.unmodifiableMap(devices);
        this.parameterRegistry = parameterRegistry;
        this.internalVariableRegistry = internalVariableRegistry;
        this.stateRegistry = stateRegistry;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public ParameterRegistry getParameterRegistry() {
        return parameterRegistry;
    }

    public InternalVariableRegistry getInternalVariableRegistry() {
        return internalVariableRegistry;
    }

    public Parameter<?> resolveParameter(TargetAddress address) {
        requireScheme(address, AddressScheme.PARAMETER);

        Device device = devices.get(address.getDeviceName());

        if (device == null) {
            throw new DriverNotFoundException("Address '" + address + "' refers to device '" + address.getDeviceName() + "' which has not yet been configured");
        }

        List<String> path = address.getParameterPath();
        Parameter<?> parameter = parameterRegistry.getParameter(device, path.get(0));

        for (int i = 1; i < path.size(); i++) {
            parameter = parameter.getSubParameter(path.get(i));
        }

        return parameter;
    }

    public InternalVariable<?> resolveVariable(TargetAddress address) {
        requireScheme(address, AddressScheme.VARIABLE);
        return internalVariableRegistry.getVariable(address.getGroupName(), address.getVariableName());
    }

    public StateRegistry getStateRegistry() {
        return stateRegistry;
    }

    public DeclaredStateValue parseDeclaredStateLiteral(TargetAddress target, String literal, String context) {
        InternalVariable<?> variable = resolveVariable(target);
        String stateTypeName = variable.getStateTypeName();

        if (stateTypeName == null) {
            throw new ConfigurationException(context + "'s target \"" + target + "\" is not a declared state variable");
        }

        return stateRegistry.parse(stateTypeName, literal);
    }

    private PreviousValueTracker resolvePreviousValueTracker(TargetAddress address) {
        requireScheme(address, AddressScheme.PREVIOUS);
        return previousValueTrackers.computeIfAbsent(address, addr -> {
            Parameter<?> parameter = resolveParameter(addr.toParameterAddress());
            return new PreviousValueTracker(parameter::getValue);
        });
    }

    public void advancePreviousValues() {
        for (PreviousValueTracker tracker : previousValueTrackers.values()) {
            tracker.advance();
        }
    }

    public Object resolveCurrentValue(TargetAddress address) {
        return resolveReader(address).get();
    }

    public Class<?> resolveType(TargetAddress address) {
        switch (address.getScheme()) {
            case VARIABLE:
                return resolveVariable(address).getType();
            case PREVIOUS:
                return resolveParameter(address.toParameterAddress()).getType();
            default:
                return resolveParameter(address).getType();
        }
    }

    public Supplier<Object> resolveReader(TargetAddress address) {
        switch (address.getScheme()) {
            case VARIABLE:
                InternalVariable<?> variable = resolveVariable(address);
                return variable::getValue;
            case PREVIOUS:
                return resolvePreviousValueTracker(address)::get;
            default:
                Parameter<?> parameter = resolveParameter(address);
                return parameter::getValue;
        }
    }

    @SuppressWarnings("unchecked")
    public Consumer<Object> resolveWriter(TargetAddress address) {
        if (address.getScheme() == AddressScheme.PREVIOUS) {
            throw new ConfigurationException("Address '" + address + "' is read-only and cannot be used as a write target");
        }

        if (address.getScheme() == AddressScheme.VARIABLE) {
            InternalVariable<Object> variable = (InternalVariable<Object>) resolveVariable(address);
            return variable::setValue;
        }

        return resolveParameter(address)::setValue;
    }

    private static void requireScheme(TargetAddress address, AddressScheme expected) {
        if (address.getScheme() != expected) {
            throw new ConfigurationException("Address '" + address + "' is not a " + expected + " address");
        }
    }

    private static final class PreviousValueTracker {
        private final Supplier<Object> liveReader;
        private Object snapshot;

        private PreviousValueTracker(Supplier<Object> liveReader) {
            this.liveReader = liveReader;
            this.snapshot = liveReader.get();
        }

        Object get() {
            return snapshot;
        }

        void advance() {
            snapshot = liveReader.get();
        }
    }
}