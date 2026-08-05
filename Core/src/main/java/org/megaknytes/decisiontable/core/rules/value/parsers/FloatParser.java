package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;

public class FloatParser implements ValueParser<Float> {
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