package com.MegaKnytes.DecisionTable.utils.xml;

import java.util.List;

/**
 * Represents a rule with a description, conditions, and actions
 */
public class Rule {
    private final String description;
    private final List<Condition> conditions;
    private final List<Action> actions;

    /**
     * Constructs a Rule object.
     *
     * @param description A description of the rule located in the XML
     * @param conditions  A list of conditions that must be met for the rule to apply
     * @param actions     A list of actions to be taken when the rule applies
     */
    public Rule(String description, List<Condition> conditions, List<Action> actions) {
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
    }

    /**
     * Gets the description of the rule
     *
     * @return The description of the rule
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of conditions for the rule
     *
     * @return The list of conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * Gets the list of actions for the rule
     *
     * @return The list of actions
     */
    public List<Action> getActions() {
        return actions;
    }
}