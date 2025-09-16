package org.megaknytes.ftc.decisiontable.core.xml.structure.ruleset;

import java.util.List;

public class Rule {
    private final String description;

    private final List<Condition> conditions;

    private final List<Action> actions;

    public Rule(String description, List<Condition> conditions, List<Action> actions) {
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
    }

    public String getDescription() {
        return description;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Action> getActions() {
        return actions;
    }
}