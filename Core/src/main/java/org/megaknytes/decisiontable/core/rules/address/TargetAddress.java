package org.megaknytes.decisiontable.core.rules.address;

import org.megaknytes.decisiontable.core.utils.exceptions.InvalidAddressException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TargetAddress {
    private final AddressScheme scheme;
    private final List<String> segments;

    private TargetAddress(AddressScheme scheme, List<String> segments) {
        this.scheme = scheme;
        this.segments = Collections.unmodifiableList(segments);
    }

    public static TargetAddress parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new InvalidAddressException("Address cannot be null or empty");
        }

        AddressScheme scheme = AddressScheme.PARAMETER;
        String path = raw;

        int colon = raw.indexOf(':');
        if (colon >= 0) {
            String prefix = raw.substring(0, colon);
            AddressScheme resolved = AddressScheme.fromPrefix(prefix);
            if (resolved == null) {
                throw new InvalidAddressException("Unknown address scheme '" + prefix + "' in '" + raw + "' (expected 'param:' or 'var:')");
            }
            scheme = resolved;
            path = raw.substring(colon + 1);
        }

        List<String> segments = getSegments(raw, path, scheme);

        return new TargetAddress(scheme, segments);
    }

    private static List<String> getSegments(String raw, String path, AddressScheme scheme) {
        List<String> segments = new ArrayList<>();
        for (String segment : path.split("\\.", -1)) {
            if (segment.isEmpty()) {
                throw new InvalidAddressException("Address '" + raw + "' has an empty path segment");
            }
            segments.add(segment);
        }

        if (scheme == AddressScheme.VARIABLE && segments.size() != 2) {
            throw new InvalidAddressException("Variable address '" + raw + "' must have exactly two segments (Group.Name), found " + segments.size());
        }
        if ((scheme == AddressScheme.PARAMETER || scheme == AddressScheme.PREVIOUS) && segments.size() < 2) {
            throw new InvalidAddressException("Parameter address '" + raw + "' must have at least two segments (Device.Parameter), found " + segments.size());
        }
        return segments;
    }

    public AddressScheme getScheme() {
        return scheme;
    }

    public List<String> getSegments() {
        return segments;
    }

    public String getGroupName() {
        requireScheme(AddressScheme.VARIABLE);
        return segments.get(0);
    }

    public String getVariableName() {
        requireScheme(AddressScheme.VARIABLE);
        return segments.get(1);
    }

    public String getDeviceName() {
        requireScheme(AddressScheme.PARAMETER);
        return segments.get(0);
    }

    public List<String> getParameterPath() {
        requireScheme(AddressScheme.PARAMETER);
        return segments.subList(1, segments.size());
    }

    public TargetAddress toParameterAddress() {
        requireScheme(AddressScheme.PREVIOUS);
        return new TargetAddress(AddressScheme.PARAMETER, segments);
    }

    private void requireScheme(AddressScheme required) {
        if (scheme != required) {
            throw new IllegalStateException("Address '" + this + "' is not a " + required + " address");
        }
    }

    @Override
    public String toString() {
        return scheme.getPrefix() + ":" + String.join(".", segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetAddress)) return false;
        TargetAddress that = (TargetAddress) o;
        return scheme == that.scheme && segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, segments);
    }
}