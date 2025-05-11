package com.decisiontable.ftc.core.xml;

import java.util.List;

/**
 * Represents a rule in the decision table.
 * <p>
 * A rule consists of a description, a list of conditions, and a list of actions.
 * The conditions are evaluated to determine if the rule should be executed,
 * and the actions are performed if the rule is executed.
 */
public class Rule {
    /**
     * Description of the rule
     */
    private final String description;

    /**
     * List of conditions for the rule
     */
    private final List<Condition> conditions;       // List of conditions for the rule

    /**
     * List of actions to be executed if the rule is satisfied
     */
    private final List<Action> actions;

    /**
     * Constructs a new rule instance.
     *
     * @param description Description of the rule
     * @param conditions  List of conditions to be evaluated
     * @param actions     List of actions to be executed if the rule is satisfied
     */
    public Rule(String description, List<Condition> conditions, List<Action> actions) {
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
    }

    /**
     * @return String description of the rule
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return List of conditions for the rule
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @return List of actions to be executed if the rule is satisfied
     */
    public List<Action> getActions() {
        return actions;
    }
}
