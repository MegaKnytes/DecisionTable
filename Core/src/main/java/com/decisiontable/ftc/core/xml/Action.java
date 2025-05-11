package com.decisiontable.ftc.core.xml;

import com.decisiontable.ftc.core.xml.parameters.Parameter;
import com.decisiontable.ftc.core.xml.values.types.ParameterValue;
import com.decisiontable.ftc.core.xml.values.Value;

/**
 * Represents an action in a decision table.
 * <p>
 * An action consists of a parameter and a value. It defines the value
 * to be set to the parameter when the action is executed. The value can
 * be a specific value or a reference to another parameter.
 */
public class Action {
    /**
     * The parameter to be set
     */
    private final Parameter<?> parameter;

    /**
     * The value to be set to the parameter
     */
    private final Value<?> value;

    /**
     * Constructs a new action instance
     *
     * @param parameter The parameter to be set
     * @param value     The value to be set to the parameter
     */
    public Action(Parameter<?> parameter, Value<?> value) {
        this.parameter = parameter;
        this.value = value;
    }


    /**
     * Executes the action by setting the value of the parameter.
     * <p>
     * This method retrieves the value from the `value` object and sets it
     * to the `parameter`. If the value is a `ParameterValue`, it retrieves
     * the actual value from the referenced parameter.
     *
     * @throws RuntimeException if there is a type mismatch when setting the value
     */
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

    /**
     * @return The value to be set
     */
    public Value<?> getValue() {
        return value;
    }
}