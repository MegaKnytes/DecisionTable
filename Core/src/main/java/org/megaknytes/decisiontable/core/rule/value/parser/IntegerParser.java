package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;

public class IntegerParser implements ValueTypeParser<Integer> {
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