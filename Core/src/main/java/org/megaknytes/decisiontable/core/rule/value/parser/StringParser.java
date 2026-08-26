package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;

public class StringParser implements ValueTypeParser<String> {
    @Override
    public String parse(String literal) {
        return literal;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}