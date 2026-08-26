package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.rule.address.AddressScheme;
import org.megaknytes.decisiontable.core.rule.address.Address;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;
import org.megaknytes.decisiontable.core.utils.exception.DriverNotFoundException;
import org.megaknytes.decisiontable.core.rule.value.type.registry.StateRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.registry.InternalVariableRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.registry.ParameterRegistry;
import org.megaknytes.decisiontable.core.rule.value.type.InternalVariable;
import org.megaknytes.decisiontable.core.rule.value.type.Parameter;
import org.megaknytes.decisiontable.core.rule.value.type.State;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SystemConfiguration {
    private final String name;

    private final Map<String, Device> devices;

    private final ParameterRegistry parameterRegistry;
    private final InternalVariableRegistry internalVariableRegistry;
    private final StateRegistry stateRegistry;

    private final Map<Address, PreviousValueTracker> previousValueTrackers = new HashMap<>();

    /**
     * @param name the name of this system configuration
     * @param devices the devices available to this configuration, keyed by device name
     * @param parameterRegistry the registry used to resolve device parameters
     * @param internalVariableRegistry the registry used to resolve internal variables
     * @param stateRegistry the registry used to parse declared state values
     */
    public SystemConfiguration(String name, Map<String, Device> devices, ParameterRegistry parameterRegistry, InternalVariableRegistry internalVariableRegistry, StateRegistry stateRegistry) {
        this.name = name;
        this.devices = Collections.unmodifiableMap(devices);
        this.parameterRegistry = parameterRegistry;
        this.internalVariableRegistry = internalVariableRegistry;
        this.stateRegistry = stateRegistry;
    }

    public String getName() {
        return name;
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public Parameter<?> getParameter(Address address) {
        requireAddressScheme(address, AddressScheme.PARAMETER);

        // Get the associated device
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

    public InternalVariable<?> getInternalVariable(Address address) {
        requireAddressScheme(address, AddressScheme.VARIABLE);
        
        return internalVariableRegistry.getVariable(address.getGroupName(), address.getVariableName());
    }

    public State getDeclaredState(Address target, String string, String context) {
        InternalVariable<?> variable = getInternalVariable(target);
        String stateTypeName = variable.getStateTypeName();

        if (stateTypeName == null) {
            throw new ConfigurationException(context + "'s target \"" + target + "\" is not a declared state variable");
        }

        return stateRegistry.parse(stateTypeName, string);
    }

    private PreviousValueTracker getPreviousValue(Address address) {
        requireAddressScheme(address, AddressScheme.PREVIOUS);
        
        return previousValueTrackers.computeIfAbsent(address, addr -> {
            Parameter<?> parameter = getParameter(addr.toParameterAddress());
            return new PreviousValueTracker(parameter::getValue);
        });
    }

    public void advancePreviousValues() {
        for (PreviousValueTracker tracker : previousValueTrackers.values()) {
            tracker.advance();
        }
    }

    public Class<?> getAddressType(Address address) {
        switch (address.getScheme()) {
            case VARIABLE:
                return getInternalVariable(address).getType();
            case PREVIOUS:
                return getParameter(address.toParameterAddress()).getType();
            default:
                return getParameter(address).getType();
        }
    }

    public Supplier<Object> getReader(Address address) {
        switch (address.getScheme()) {
            case VARIABLE:
                InternalVariable<?> variable = getInternalVariable(address);
                return variable::getValue;
            case PREVIOUS:
                return getPreviousValue(address)::get;
            default:
                Parameter<?> parameter = getParameter(address);
                return parameter::getValue;
        }
    }

    @SuppressWarnings("unchecked")
    public Consumer<Object> getWriter(Address address) {
        if (address.getScheme() == AddressScheme.PREVIOUS) {
            throw new ConfigurationException("Address '" + address + "' is read-only and cannot be written");
        }

        if (address.getScheme() == AddressScheme.VARIABLE) {
            InternalVariable<Object> variable = (InternalVariable<Object>) getInternalVariable(address);
            return variable::setValue;
        }

        return getParameter(address)::setValue;
    }

    private static void requireAddressScheme(Address address, AddressScheme expected) {
        if (address.getScheme() != expected) {
            throw new ConfigurationException("Address '" + address + "' is not a " + expected + " address");
        }
    }

    private static final class PreviousValueTracker {
        private final Supplier<Object> liveReader;
        private Object snapshot;

        private PreviousValueTracker(Supplier<Object> liveValue) {
            this.liveReader = liveValue;
            this.snapshot = liveValue.get();
        }

        Object get() {
            return snapshot;
        }

        void advance() {
            snapshot = liveReader.get();
        }
    }
}