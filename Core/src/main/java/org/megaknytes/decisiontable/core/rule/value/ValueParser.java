package org.megaknytes.decisiontable.core.rule.value;

import org.megaknytes.decisiontable.core.rule.address.Address;
import org.megaknytes.decisiontable.core.utils.exception.AmbiguousValueSourceException;
import org.w3c.dom.Element;

/**
 * Turns an XML value or reference into a Value object
 */
public class ValueParser {
    private final ValueParserRegistry parserRegistry;

    public ValueParser(ValueParserRegistry parserRegistry) {
        this.parserRegistry = parserRegistry;
    }

    public <T> Value<T> parse(String value, Element element, String ref, Class<T> expectedType, String errorContext) {
        boolean hasValue = value != null;
        boolean hasRef = ref != null;

        if (hasValue == hasRef) {
            throw new AmbiguousValueSourceException(errorContext + " must specify exactly one \"value\" or \"ref\"");
        }

        if (hasRef) {
            Address address = Address.parse(ref);
            return Value.reference(address);
        }

        ValueTypeParser<T> parser = parserRegistry.getParser(expectedType);

        if (parser instanceof XMLValueTypeParser) {
            return Value.literal(((XMLValueTypeParser<T>) parser).parse(element));
        }

        return Value.literal(parser.parse(value));
    }
}