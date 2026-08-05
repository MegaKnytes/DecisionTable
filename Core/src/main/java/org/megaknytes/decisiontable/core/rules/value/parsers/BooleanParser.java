package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;

import java.util.Arrays;
import java.util.List;

public class BooleanParser implements ValueParser<Boolean> {
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

    @Override
    public List<String> getPossibleValues() {
        return Arrays.asList("true", "false");
    }
}