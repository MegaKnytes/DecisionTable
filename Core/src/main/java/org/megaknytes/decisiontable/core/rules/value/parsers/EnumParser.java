package org.megaknytes.decisiontable.core.rules.value.parsers;

import org.megaknytes.decisiontable.core.rules.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exceptions.TypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumParser<T extends Enum<T>> implements ValueParser<T> {
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

        throw new TypeMismatchException("'" + literal + "' is not a valid constant of enum type " + enumType.getName());
    }

    @Override
    public Class<T> getType() {
        return enumType;
    }

    @Override
    public List<String> getPossibleValues() {
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }
}