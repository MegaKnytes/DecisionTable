package org.megaknytes.ftc.decisiontable.core.xml.values;

import org.megaknytes.ftc.decisiontable.core.utils.XMLHelperMethods;
import org.megaknytes.ftc.decisiontable.core.utils.discovery.DTClassDiscovery;
import org.megaknytes.ftc.decisiontable.core.utils.exceptions.NoRegisteredParserException;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.EnumValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.InternalVariableValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.ParameterValue;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValueHandler {
    private static final Map<Class<?>, Value<?>> PARSER_CACHE = DTClassDiscovery.getValueParserClasses();
    private static final Logger LOGGER = Logger.getLogger(ValueHandler.class.getName());

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Value<T> parseValue(Node parameterNode, Class<T> expectedType) throws IllegalAccessException, InstantiationException {
        LOGGER.log(Level.INFO, "Beginning to parse value...");

        List<Element> specialParameterNodes = XMLHelperMethods.getElementNodes(parameterNode.getChildNodes());

        if (specialParameterNodes.isEmpty()) {
            if (expectedType.isEnum()) {
                EnumValue enumValue = new EnumValue();
                enumValue.setEnumType(expectedType);
                enumValue.parseValue(parameterNode);
                return (Value<T>) enumValue;
            }

            Value<?> cachedParser = PARSER_CACHE.get(expectedType);
            if (cachedParser == null) {
                throw new NoRegisteredParserException("No value parser found for type: " + expectedType.getName());
            }

            Value<T> newParser = (Value<T>) cachedParser.getClass().newInstance();
            newParser.parseValue(parameterNode);

            return newParser;
        }


        switch (specialParameterNodes.get(0).getNodeName()) {
            case "InternalVariable":
                InternalVariableValue<T> internalVarValue = new InternalVariableValue<>();
                internalVarValue.parseValue(parameterNode);
                return internalVarValue;
            case "Parameter":
                ParameterValue<T> paramValue = new ParameterValue<>();
                paramValue.parseValue(parameterNode);
                return paramValue;
            default:
                return null;
        }
    }
}