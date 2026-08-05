package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;

public class DoubleParser implements ValueParser<Double> {
    @Override
    public Double parse(String literal) {
        try {
            return Double.parseDouble(literal);
        } catch (NumberFormatException e) {
            throw new TypeMismatchException("'" + literal + "' is not a valid Double");
        }
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}