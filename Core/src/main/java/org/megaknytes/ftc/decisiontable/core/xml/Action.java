package org.megaknytes.ftc.decisiontable.core.xml;

import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.values.types.ParameterValue;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

public class Action {
    private final Parameter<?> parameter;
    private final Value<?> value;

    public Action(Parameter<?> parameter, Value<?> value) {
        this.parameter = parameter;
        this.value = value;
    }


    public Value<?> getValue() {
        return value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void execute() {
        try {
            if (value instanceof ParameterValue) {
                ParameterValue paramValue = (ParameterValue) value;
                ((Parameter) parameter).setValue(paramValue.getValue());
            } else {
                ((Parameter) parameter).setValue(value.getValue());
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Type mismatch executing action on parameter " +
                    parameter.getName() + ": " + e.getMessage(), e);
        }
    }
}