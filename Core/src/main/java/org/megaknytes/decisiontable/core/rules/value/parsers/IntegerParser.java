package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;

public class IntegerParser implements ValueParser<Integer> {
    @Override
    public Integer parse(String literal) {
        try {
            return Integer.parseInt(literal);
        } catch (NumberFormatException e) {
            throw new TypeMismatchException("'" + literal + "' is not a valid Integer");
        }
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}