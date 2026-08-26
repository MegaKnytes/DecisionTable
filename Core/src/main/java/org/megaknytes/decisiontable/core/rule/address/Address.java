package org.megaknytes.decisiontable.core.rule.address;

import org.megaknytes.decisiontable.core.utils.exception.InvalidAddressException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A reference to a parameter, internal variable, or previous value (anything not literal)
 */
public final class Address {
    private final AddressScheme scheme;
    private final List<String> segments;

    private Address(AddressScheme scheme, List<String> segments) {
        this.scheme = scheme;
        this.segments = Collections.unmodifiableList(segments);
    }

    /**
     * Parses an address string into an Address.
     *
     * @param raw the address string to parse
     * @return the parsed TargetAddress
     */
    public static Address parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new InvalidAddressException("Address cannot be null or empty");
        }

        int colon = raw.indexOf(':');
        if (colon >= 0) {
            String prefix = raw.substring(0, colon);
            AddressScheme scheme = AddressScheme.fromPrefix(prefix);
            if (scheme == null) {
                throw new InvalidAddressException("Unknown address prefix '" + prefix + "' in '" + raw + "' (expected 'param:', 'prev:', or 'var:')");
            }
            String path = raw.substring(colon + 1);

            List<String> segments = getSegments(raw, path, scheme);

            return new Address(scheme, segments);
        } else {
            throw new InvalidAddressException("Address '" + raw + "' is missing a scheme prefix (expected 'param:', 'prev:', or 'var:')");
        }
    }

    /**
     * Split a path into its path segments and validate the segment count against the determined scheme
     *
     * @param raw the original address
     * @param path the portion of raw after the prefix
     * @param scheme the scheme that the path is being parsed for
     * @return the valid segments of path
     */
    private static List<String> getSegments(String raw, String path, AddressScheme scheme) {
        List<String> segments = new ArrayList<>();
        for (String segment : path.split("\\.", -1)) {
            if (segment.isEmpty()) {
                throw new InvalidAddressException("Address '" + raw + "' has an empty segment, check for trailing or consecutive periods");
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

    public Address toParameterAddress() {
        requireScheme(AddressScheme.PREVIOUS);
        return new Address(AddressScheme.PARAMETER, segments);
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
        if (!(o instanceof Address)) return false;
        Address that = (Address) o;
        return scheme == that.scheme && segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheme, segments);
    }
}