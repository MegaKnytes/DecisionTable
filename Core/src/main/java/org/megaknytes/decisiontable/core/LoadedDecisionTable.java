package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.rules.Rule;
import org.megaknytes.decisiontable.core.utils.DecisionTableOpModeFlavor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LoadedDecisionTable {
    private final String name;
    private final boolean enabled;
    private final DecisionTableOpModeFlavor flavor;
    private final String systemConfigurationName;
    private final String transitionTarget;
    private final List<Rule> rules;

    public LoadedDecisionTable(String name, boolean enabled, DecisionTableOpModeFlavor flavor, String systemConfigurationName, String transitionTarget, List<Rule> rules) {
        this.name = name;
        this.enabled = enabled;
        this.flavor = flavor;
        this.systemConfigurationName = systemConfigurationName;
        this.transitionTarget = transitionTarget;
        this.rules = Collections.unmodifiableList(rules);
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DecisionTableOpModeFlavor getFlavor() {
        return flavor;
    }

    public String getSystemConfigurationName() {
        return systemConfigurationName;
    }

    public String getTransitionTarget() {
        return transitionTarget;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<Rule> getRulesInPriorityOrder() {
        List<Rule> sorted = new ArrayList<>(rules);
        sorted.sort(Comparator.comparingInt(Rule::getPriority));
        return sorted;
    }
}