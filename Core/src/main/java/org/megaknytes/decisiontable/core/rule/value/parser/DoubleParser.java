package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;

public class DoubleParser implements ValueTypeParser<Double> {
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