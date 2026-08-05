package org.megaknytes.decisiontable.core.rules.value;

import org.megaknytes.decisiontable.core.rules.value.parsers.BooleanParser;
import org.megaknytes.decisiontable.core.rules.value.parsers.DoubleParser;
import org.megaknytes.decisiontable.core.rules.value.parsers.EnumParser;
import org.megaknytes.decisiontable.core.rules.value.parsers.FloatParser;
import org.megaknytes.decisiontable.core.rules.value.parsers.IntegerParser;
import org.megaknytes.decisiontable.core.rules.value.parsers.StringParser;
import org.megaknytes.decisiontable.core.utils.exceptions.NoRegisteredParserException;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueParserRegistry {
    private final Map<Class<?>, ValueParser<?>> parsers = new ConcurrentHashMap<>();

    public ValueParserRegistry() {
        resetToDefaults();
    }

    public final void resetToDefaults() {
        parsers.clear();
        registerParser(new BooleanParser());
        registerParser(new DoubleParser());
        registerParser(new FloatParser());
        registerParser(new IntegerParser());
        registerParser(new StringParser());
    }

    public <T> void registerParser(ValueParser<T> parser) {
        parsers.put(parser.getType(), parser);
    }

    @SuppressWarnings("unchecked")
    public <T> ValueParser<T> getParser(Class<T> type) {
        ValueParser<?> parser = parsers.get(type);
        if (parser != null) {
            return (ValueParser<T>) parser;
        }

        if (type.isEnum()) {
            @SuppressWarnings("rawtypes")
            EnumParser enumParser = new EnumParser(type);
            return (ValueParser<T>) enumParser;
        }

        throw new NoRegisteredParserException("No value parser registered for type: " + type.getName());
    }

    public Map<Class<?>, ValueParser<?>> getRegisteredParsers() {
        return Collections.unmodifiableMap(parsers);
    }
}