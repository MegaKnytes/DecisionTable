package org.megaknytes.ftc.decisiontable.core.xml.values;

import org.megaknytes.ftc.decisiontable.core.utils.DTClassDiscoveryUtil;
import org.megaknytes.ftc.decisiontable.core.utils.XMLUtils;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.ConfigurationException;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.NoRegisteredParserException;
import org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes.EnumValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.valuetypes.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class ValueParser {
    private static final Map<Class<?>, Value<?>> parserCache = DTClassDiscoveryUtil.getInstance().getValueParserClasses();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Value<T> parseValue(Node parameterNode, Class<T> expectedType) throws IllegalAccessException, InstantiationException {
        if (parameterNode == null) {
            throw new ConfigurationException("Parameter node cannot be null");
        }

        if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) parameterNode;
            if (XMLUtils.hasParameterElement(element)) {
                ParameterValue<T> paramValue = new ParameterValue<>();
                paramValue.parseValue(parameterNode);
                return paramValue;
            }
        }

        if (expectedType.isEnum()) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) expectedType;
            EnumValue enumValue = new EnumValue();
            enumValue.setEnumType(enumClass);
            enumValue.parseValue(parameterNode);
            return (Value<T>) enumValue;
        }

        Value<?> cachedParser = parserCache.get(expectedType);
        if (cachedParser == null) {
            throw new NoRegisteredParserException("No value parser found for type: " + expectedType.getName());
        }

        Value<T> newParser = (Value<T>) cachedParser.getClass().newInstance();
        newParser.parseValue(parameterNode);

        return newParser;
    }
}