package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.rule.Action;
import org.megaknytes.decisiontable.core.rule.Rule;
import org.megaknytes.decisiontable.core.rule.address.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The result of an evaluate() cycle after one execution by the DecisionTableEvaluator
 */
public final class EvaluationResult {
    private final Map<Rule, List<Action>> matchedRules;
    private final List<Conflict> conflicts;

    public EvaluationResult(Map<Rule, List<Action>> matchedRules, long evaluationTimeMillis) {
        this.matchedRules = matchedRules;
        this.conflicts = Conflict.findConflicts(matchedRules);
    }

    public String getMatchedRuleNames() {
        return matchedRules.keySet().stream()
                .map(Rule::getName)
                .collect(Collectors.joining(", "));
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

    /**
     * A conflict occurs when two or more rules attempt to write to the same address.
     * <p>
     * In this case, the rule with the highest priority "wins" the conflict, as it is executed last
     */
    public static final class Conflict {
        private final Address address;
        private final List<Rule> rules;

        private Conflict(Address address, List<Rule> rules) {
            this.address = address;
            this.rules = Collections.unmodifiableList(rules);
        }

        private static List<Conflict> findConflicts(Map<Rule, List<Action>> matchedRules) {
            Map<Address, List<Rule>> writersByTarget = new LinkedHashMap<>();

            for (Map.Entry<Rule, List<Action>> entry : matchedRules.entrySet()) {
                for (Action action : entry.getValue()) {
                    writersByTarget.computeIfAbsent(action.getAddress(), k -> new ArrayList<>()).add(entry.getKey());
                }
            }

            return writersByTarget.entrySet().stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .map(entry -> new Conflict(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }

        public Address getTargetAddress() {
            return address;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public Rule getWinningRule() {
            return rules.get(rules.size() - 1);
        }

        @Override
        public String toString() {
            return address + " (winner: '" + getWinningRule().getName() + "')";
        }
    }
}