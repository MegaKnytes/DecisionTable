package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.rule.value.snapshot.Snapshot;
import org.megaknytes.decisiontable.core.rule.Action;
import org.megaknytes.decisiontable.core.rule.Rule;
import org.megaknytes.decisiontable.core.utils.device.Device;
import org.megaknytes.decisiontable.core.utils.device.UpdatableDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DecisionTable {
    private final String name;
    private final List<Rule> rules;
    private final Snapshot snapshot;

    public DecisionTable(String name, List<Rule> rules, Snapshot snapshot) {
        this.name = name;
        this.snapshot = snapshot;

        List<Rule> sorted = new ArrayList<>(rules);
        sorted.sort(Comparator.comparingInt(Rule::getPriority));
        this.rules = Collections.unmodifiableList(sorted);
    }

    public EvaluationResult evaluate(SystemConfiguration configuration) {
        long startTime = System.nanoTime();
        Iterable<? extends Device> devices = configuration == null ? Collections.emptyList() : configuration.getDevices().values();

        // Update every device once-per-loop that tells us it needs to be updated
        for (Device device : devices) {
            if (device instanceof UpdatableDevice) {
                ((UpdatableDevice) device).update();
            }
        }

        snapshot.refreshAll();

        Map<Rule, List<Action>> matchedRules = new LinkedHashMap<>();

        // Evaluate the rules in ascending priority order, to ensure that if a conflict occurs the rule with the highest priority "wins".
        for (Rule rule : rules) {
            if (rule.evaluate()) {
                matchedRules.put(rule, rule.getActions());
            }
        }

        for (Map.Entry<Rule, List<Action>> entry : matchedRules.entrySet()) {
            for (Action action : entry.getValue()) {
                action.execute();
            }
        }

        // Advance the state of each previous value tracker, useful for things like gamepads where you only want to trigger a state-machine one time.
        if (configuration != null) {
            configuration.advancePreviousValues();
        }

        return new EvaluationResult(matchedRules, (System.nanoTime() - startTime) / 1_000_000);
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }
}