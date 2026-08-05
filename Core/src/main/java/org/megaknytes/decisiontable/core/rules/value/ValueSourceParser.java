package org.megaknytes.decisiontable.core.rules.value;

import org.megaknytes.decisiontable.core.rules.address.TargetAddress;
import org.megaknytes.decisiontable.core.utils.exceptions.AmbiguousValueSourceException;

public class ValueSourceParser {
    private final ValueParserRegistry parserRegistry;

    public ValueSourceParser(ValueParserRegistry parserRegistry) {
        this.parserRegistry = parserRegistry;
    }

    public <T> ValueSource<T> parse(String value, String ref, Class<T> expectedType, String errorContext) {
        boolean hasValue = value != null;
        boolean hasRef = ref != null;

        if (hasValue == hasRef) {
            throw new AmbiguousValueSourceException(errorContext + " must specify exactly one of \"value\" or \"ref\" (found " + (hasValue ? "both" : "neither") + ")");
        }

        if (hasRef) {
            TargetAddress address = TargetAddress.parse(ref);
            return ValueSource.reference(address);
        }

        ValueParser<T> parser = parserRegistry.getParser(expectedType);

        return ValueSource.literal(parser.parse(value));
    }
}