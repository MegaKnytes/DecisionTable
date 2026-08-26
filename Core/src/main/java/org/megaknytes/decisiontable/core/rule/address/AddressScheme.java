package org.megaknytes.decisiontable.core.rule.address;

public enum AddressScheme {
    PARAMETER("param"),
    VARIABLE("var"),
    PREVIOUS("prev");

    private final String prefix;

    AddressScheme(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static AddressScheme fromPrefix(String prefix) {
        for (AddressScheme scheme : values()) {
            if (scheme.prefix.equals(prefix)) {
                return scheme;
            }
        }
        return null;
    }
}