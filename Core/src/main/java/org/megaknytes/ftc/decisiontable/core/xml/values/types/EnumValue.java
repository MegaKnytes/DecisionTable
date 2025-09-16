package org.megaknytes.ftc.decisiontable.core.xml.values.types;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;
import org.w3c.dom.Node;

public class EnumValue<T extends Enum<T>> implements Value<T> {
    private T value;
    private Class<T> enumType;

    public EnumValue() {
    }

    @Override
    public T parseValue(Node parameterNode) {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        String enumValue = parameterNode.getTextContent().trim();

        try {
            T[] enumConstants = enumType.getEnumConstants();
            assert enumConstants != null;
            for (T constant : enumConstants) {
                if (constant.name().equalsIgnoreCase(enumValue)) {
                    this.value = constant;
                    return this.value;
                }
            }
            throw new ConfigurationException("Invalid enum value: " + enumValue + " for enum type: " + enumType.getName());
        } catch (NullPointerException e) {
            throw new ConfigurationException("Enum type has not been properly initialized, please check your configuration");
        }
    }

    @Override
    public void setValue(T value) {
        this.value=value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public Class<?> getType() {
        if (enumType == null) {
            return Enum.class;
        }
        return enumType;
    }

    public void setEnumType(Class<T> enumType) {
        this.enumType = enumType;
    }
}