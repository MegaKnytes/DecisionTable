package org.megaknytes.decisiontable.ftc.xml.structure.ruleset;

import org.megaknytes.decisiontable.ftc.utils.exceptions.TypeMismatchException;
import org.megaknytes.decisiontable.ftc.xml.structure.parameters.Parameter;
import org.megaknytes.decisiontable.ftc.xml.values.Value;

public class Action {

    private final Parameter<?> parameter;

    private final Value<?> value;

    public Action(Parameter<?> parameter, Value<?> value) {
        this.parameter = parameter;
        this.value = value;
    }

    public void execute() {
        try {
            Object valueObj = value.getValue();
            if (valueObj != null) {
                parameter.setValue(valueObj);
            }
        } catch (ClassCastException e) {
            throw new TypeMismatchException("Type mismatch when executing action: " + e.getMessage());
        }
    }

    public Parameter<?> getParameter() {
        return parameter;
    }

}