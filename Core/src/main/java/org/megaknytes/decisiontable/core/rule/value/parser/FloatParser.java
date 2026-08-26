package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;

public class FloatParser implements ValueTypeParser<Float> {
    @Override
    public Float parse(String literal) {
        try {
            return Float.parseFloat(literal);
        } catch (NumberFormatException e) {
            throw new TypeMismatchException("'" + literal + "' is not a valid Float");
        }
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }
}