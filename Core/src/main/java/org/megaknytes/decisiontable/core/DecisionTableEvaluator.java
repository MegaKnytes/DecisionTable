package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.config.LoadedSystemConfiguration;
import org.megaknytes.decisiontable.core.rules.Rule;
import org.megaknytes.decisiontable.core.rules.Action;
import org.megaknytes.decisiontable.core.utils.Device;
import org.megaknytes.decisiontable.core.utils.UpdatableDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecisionTableEvaluator {
    public EvaluationResult evaluate(LoadedDecisionTable table) {
        return evaluate(table, Collections.emptyList(), null);
    }

    public EvaluationResult evaluate(LoadedDecisionTable table, Iterable<? extends Device> devices) {
        return evaluate(table, devices, null);
    }

    public EvaluationResult evaluate(LoadedDecisionTable table, LoadedSystemConfiguration configuration) {
        Iterable<? extends Device> devices = configuration == null ? Collections.emptyList() : configuration.getDevices().values();
        return evaluate(table, devices, configuration);
    }

    private EvaluationResult evaluate(LoadedDecisionTable table, Iterable<? extends Device> devices, LoadedSystemConfiguration configuration) {
        List<EvaluationResult.EvaluationError> errors = new ArrayList<>();

        updateDevices(devices, errors);

        Map<Rule, List<Action>> matchedRules = new LinkedHashMap<>();

        for (Rule rule : table.getRulesInPriorityOrder()) {
            if (evaluateRule(rule, errors)) {
                matchedRules.put(rule, rule.getActions());
            }
        }

        for (Map.Entry<Rule, List<Action>> entry : matchedRules.entrySet()) {
            for (Action action : entry.getValue()) {
                executeAction(entry.getKey(), action, errors);
            }
        }

        if (configuration != null) {
            configuration.advancePreviousValues();
        }

        return new EvaluationResult(matchedRules, errors);
    }

    private static boolean evaluateRule(Rule rule, List<EvaluationResult.EvaluationError> errors) {
        try {
            return rule.evaluate();
        } catch (RuntimeException e) {
            errors.add(new EvaluationResult.EvaluationError("Rule '" + rule.getName() + "' condition", e));
            return false;
        }
    }

    private static void executeAction(Rule rule, Action action, List<EvaluationResult.EvaluationError> errors) {
        try {
            action.execute();
        } catch (RuntimeException e) {
            errors.add(new EvaluationResult.EvaluationError("Rule '" + rule.getName() + "' action", e));
        }
    }

    private static void updateDevices(Iterable<? extends Device> devices, List<EvaluationResult.EvaluationError> errors) {
        if (devices == null) {
            return;
        }

        for (Device device : devices) {
            if (device instanceof UpdatableDevice) {
                try {
                    ((UpdatableDevice) device).update();
                } catch (RuntimeException e) {
                    errors.add(new EvaluationResult.EvaluationError("Device '" + device.getDeviceName() + "' update", e));
                }
            }
        }
    }
}