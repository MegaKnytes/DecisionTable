package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;

public class StringParser implements ValueParser<String> {
    @Override
    public String parse(String literal) {
        return literal;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}