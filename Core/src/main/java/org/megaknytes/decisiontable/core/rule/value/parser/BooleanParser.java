package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;

public class BooleanParser implements ValueTypeParser<Boolean> {
    @Override
    public Boolean parse(String literal) {
        if (!"true".equalsIgnoreCase(literal) && !"false".equalsIgnoreCase(literal)) {
            throw new TypeMismatchException("'" + literal + "' is not a valid Boolean (expected 'true' or 'false')");
        }

        return Boolean.parseBoolean(literal);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}