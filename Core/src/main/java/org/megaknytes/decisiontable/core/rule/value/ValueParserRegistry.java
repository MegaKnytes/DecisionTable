package org.megaknytes.decisiontable.core.rule.value;

import org.megaknytes.decisiontable.core.rule.value.parser.BooleanParser;
import org.megaknytes.decisiontable.core.rule.value.parser.DoubleParser;
import org.megaknytes.decisiontable.core.rule.value.parser.EnumParser;
import org.megaknytes.decisiontable.core.rule.value.parser.FloatParser;
import org.megaknytes.decisiontable.core.rule.value.parser.IntegerParser;
import org.megaknytes.decisiontable.core.rule.value.parser.StringParser;
import org.megaknytes.decisiontable.core.utils.exception.NoRegisteredParserException;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry of all discovered ValueTypeParsers
 */
public class ValueParserRegistry {
    private final Map<Class<?>, ValueTypeParser<?>> parsers = new ConcurrentHashMap<>();

    public ValueParserRegistry() {
        registerParser(new BooleanParser());
        registerParser(new DoubleParser());
        registerParser(new FloatParser());
        registerParser(new IntegerParser());
        registerParser(new StringParser());
    }

    public <T> void registerParser(ValueTypeParser<T> parser) {
        parsers.put(parser.getType(), parser);
    }

    /**
     * Resolves the ValueTypeParser for the given type
     * <p>
     * If no parser is registered for type but type is an enum, a new EnumParser for it is
     * returned instead
     *
     * @param type the type to resolve a parser for
     * @return a parser that produces values of type
     */
    @SuppressWarnings("unchecked")
    public <T> ValueTypeParser<T> getParser(Class<T> type) {
        ValueTypeParser<?> parser = parsers.get(type);
        if (parser != null) {
            return (ValueTypeParser<T>) parser;
        }

        if (type.isEnum()) {
            @SuppressWarnings("rawtypes")
            EnumParser enumParser = new EnumParser(type);
            return (ValueTypeParser<T>) enumParser;
        }

        throw new NoRegisteredParserException("No value parser registered for type: " + type.getName());
    }

    public Map<Class<?>, ValueTypeParser<?>> getRegisteredParsers() {
        return Collections.unmodifiableMap(parsers);
    }
}