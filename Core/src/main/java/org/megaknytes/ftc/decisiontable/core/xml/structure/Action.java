package org.megaknytes.ftc.decisiontable.core.xml.structure;

import org.megaknytes.ftc.decisiontable.core.utils.exceptions.TypeMismatchException;
import org.megaknytes.ftc.decisiontable.core.xml.parameters.Parameter;
import org.megaknytes.ftc.decisiontable.core.xml.values.Value;

public class Action {

    private final Parameter<?> parameter;

    private final Value<?> value;

    public Action(Parameter<?> parameter, Value<?> value) {
        this.parameter = parameter;
        this.value = value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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