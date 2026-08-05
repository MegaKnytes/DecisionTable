package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.rules.Action;
import org.megaknytes.decisiontable.core.rules.Rule;
import org.megaknytes.decisiontable.core.rules.address.TargetAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EvaluationResult {
    private final Map<Rule, List<Action>> matchedRules;
    private final List<Conflict> conflicts;
    private final List<EvaluationError> errors;

    public EvaluationResult(Map<Rule, List<Action>> matchedRules, List<EvaluationError> errors) {
        this.matchedRules = matchedRules;
        this.conflicts = findConflicts(matchedRules);
        this.errors = Collections.unmodifiableList(errors);
    }

    public int getMatchedRuleCount() {
        return matchedRules.size();
    }

    public String getMatchedRuleNames() {
        return matchedRules.keySet().stream()
                .map(Rule::getName)
                .collect(Collectors.joining(", "));
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

    public List<EvaluationError> getErrors() {
        return errors;
    }

    private static List<Conflict> findConflicts(Map<Rule, List<Action>> matchedRules) {
        Map<TargetAddress, List<Rule>> writersByTarget = new LinkedHashMap<>();

        for (Map.Entry<Rule, List<Action>> entry : matchedRules.entrySet()) {
            for (Action action : entry.getValue()) {
                writersByTarget.computeIfAbsent(action.getTargetAddress(), k -> new ArrayList<>()).add(entry.getKey());
            }
        }

        return writersByTarget.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> new Conflict(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static final class Conflict {
        private final TargetAddress targetAddress;
        private final List<Rule> rules;

        private Conflict(TargetAddress targetAddress, List<Rule> rules) {
            this.targetAddress = targetAddress;
            this.rules = Collections.unmodifiableList(rules);
        }

        public TargetAddress getTargetAddress() {
            return targetAddress;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public Rule getWinningRule() {
            return rules.get(rules.size() - 1);
        }

        @Override
        public String toString() {
            return targetAddress + " (winner: '" + getWinningRule().getName() + "')";
        }
    }

    public static final class EvaluationError {
        private final String source;
        private final RuntimeException cause;

        EvaluationError(String source, RuntimeException cause) {
            this.source = source;
            this.cause = cause;
        }

        public String getSource() {
            return source;
        }

        public RuntimeException getCause() {
            return cause;
        }

        @Override
        public String toString() {
            return source + ": " + cause.getClass().getSimpleName() + (cause.getMessage() != null ? " - " + cause.getMessage() : "");
        }
    }
}