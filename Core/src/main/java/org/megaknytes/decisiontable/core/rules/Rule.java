package org.megaknytes.decisiontable.core.rules;

import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;

import java.util.Collections;
import java.util.List;

public final class Rule {
    private final String name;
    private final String groupName;
    private final int priority;

    private final ConditionExpression condition;
    private final List<Action> actions;

    public Rule(String name, int priority, ConditionExpression condition, List<Action> actions) {
        this(name, null, priority, condition, actions);
    }

    public Rule(String name, String groupName, int priority, ConditionExpression condition, List<Action> actions) {
        this.name = name;
        this.groupName = groupName;
        this.priority = priority;
        this.condition = condition;
        this.actions = Collections.unmodifiableList(actions);
    }

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getPriority() {
        return priority;
    }

    public List<Action> getActions() {
        return actions;
    }

    public boolean evaluate() {
        return condition.evaluate();
    }
}