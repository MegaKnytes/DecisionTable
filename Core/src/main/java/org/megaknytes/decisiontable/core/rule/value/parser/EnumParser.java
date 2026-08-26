package org.megaknytes.decisiontable.core.rule.value.parser;

import org.megaknytes.decisiontable.core.rule.value.ValueTypeParser;
import org.megaknytes.decisiontable.core.utils.exception.TypeMismatchException;

public class EnumParser<T extends Enum<T>> implements ValueTypeParser<T> {
    private final Class<T> enumType;

    public EnumParser(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T parse(String literal) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(literal)) {
                return constant;
            }
        }

        throw new TypeMismatchException("'" + literal + "' is not a valid constant of enum " + enumType.getName());
    }

    @Override
    public Class<T> getType() {
        return enumType;
    }
}