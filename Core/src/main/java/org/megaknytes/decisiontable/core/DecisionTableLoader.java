package org.megaknytes.decisiontable.core;

import org.megaknytes.decisiontable.core.rules.address.TargetAddress;
import org.megaknytes.decisiontable.core.config.LoadedSystemConfiguration;
import org.megaknytes.decisiontable.core.rules.Rule;
import org.megaknytes.decisiontable.core.rules.Action;
import org.megaknytes.decisiontable.core.rules.expressions.logical.AlwaysExpression;
import org.megaknytes.decisiontable.core.rules.expressions.logical.AndExpression;
import org.megaknytes.decisiontable.core.rules.expressions.ComparisonExpression;
import org.megaknytes.decisiontable.core.rules.expressions.ConditionExpression;
import org.megaknytes.decisiontable.core.rules.expressions.logical.NotExpression;
import org.megaknytes.decisiontable.core.rules.expressions.logical.OnceExpression;
import org.megaknytes.decisiontable.core.rules.expressions.logical.OrExpression;
import org.megaknytes.decisiontable.core.rules.expressions.logical.XOrExpression;
import org.megaknytes.decisiontable.core.rules.value.DeclaredStateValue;
import org.megaknytes.decisiontable.core.rules.value.ValueParserRegistry;
import org.megaknytes.decisiontable.core.rules.value.ValueSource;
import org.megaknytes.decisiontable.core.rules.value.ValueSourceParser;
import org.megaknytes.decisiontable.core.utils.xml.XmlDocumentLoader;
import org.megaknytes.decisiontable.core.utils.xml.XmlElements;
import org.megaknytes.decisiontable.core.utils.DecisionTableOpModeFlavor;
import org.megaknytes.decisiontable.core.utils.exceptions.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class DecisionTableLoader {
    private final ValueSourceParser valueSourceParser;
    private final XmlDocumentLoader xmlDocumentLoader;

    public DecisionTableLoader(ValueParserRegistry valueParserRegistry) {
        this.valueSourceParser = new ValueSourceParser(valueParserRegistry);
        this.xmlDocumentLoader = new XmlDocumentLoader();
    }

    public LoadedDecisionTable load(File file, LoadedSystemConfiguration configuration) {
        return load(xmlDocumentLoader.parse(file), configuration);
    }

    public LoadedDecisionTable load(Document document, LoadedSystemConfiguration configuration) {
        Element root = document.getDocumentElement();
        String name = root.getAttribute("name");
        boolean enabled = parseXsdBoolean(root.getAttribute("enabled"));
        DecisionTableOpModeFlavor flavor = DecisionTableOpModeFlavor.valueOf(root.getAttribute("type"));
        String systemConfigurationName = root.getAttribute("systemConfiguration");
        String transitionTarget = XmlElements.optionalAttribute(root, "transitionTarget");

        List<Rule> rules = new ArrayList<>();
        Set<String> seenRuleNames = new HashSet<>();

        for (Element rulesSection : XmlElements.childElementsNamed(root, "Rules")) {
            for (Element child : XmlElements.childElements(rulesSection)) {
                if (child.getTagName().equals("RuleGroup")) {
                    parseRuleGroup(child, rules, seenRuleNames, configuration);
                } else {
                    addRule(child, null, rules, seenRuleNames, configuration);
                }
            }
        }

        return new LoadedDecisionTable(name, enabled, flavor, systemConfigurationName, transitionTarget, rules);
    }

    private void parseRuleGroup(Element groupElement, List<Rule> rules, Set<String> seenRuleNames, LoadedSystemConfiguration configuration) {
        String groupName = groupElement.getAttribute("name");
        String enabledAttribute = XmlElements.optionalAttribute(groupElement, "enabled");
        boolean enabled = enabledAttribute == null || parseXsdBoolean(enabledAttribute);

        if (!enabled) {
            return;
        }

        for (Element ruleElement : XmlElements.childElementsNamed(groupElement, "Rule")) {
            addRule(ruleElement, groupName, rules, seenRuleNames, configuration);
        }
    }

    private void addRule(Element ruleElement, String groupName, List<Rule> rules, Set<String> seenRuleNames, LoadedSystemConfiguration configuration) {
        String ruleName = ruleElement.getAttribute("name");

        if (!seenRuleNames.add(ruleName)) {
            throw new ConfigurationException("Duplicate rule name: " + ruleName);
        }

        rules.add(parseRule(ruleName, groupName, ruleElement, configuration));
    }

    private Rule parseRule(String ruleName, String groupName, Element ruleElement, LoadedSystemConfiguration configuration) {
        String priorityAttribute = XmlElements.optionalAttribute(ruleElement, "priority");
        int priority = priorityAttribute != null ? Integer.parseInt(priorityAttribute) : 0;

        Element conditionElement = XmlElements.childElementsNamed(ruleElement, "Condition").get(0);
        Element actionElement = XmlElements.childElementsNamed(ruleElement, "Action").get(0);

        ConditionExpression condition = parseCondition(conditionElement, configuration);
        List<Action> actions = parseActions(actionElement, configuration);

        return new Rule(ruleName, groupName, priority, condition, actions);
    }

    private ConditionExpression parseCondition(Element conditionElement, LoadedSystemConfiguration configuration) {
        List<ConditionExpression> children = parseExprList(XmlElements.childElements(conditionElement), configuration);
        return children.size() == 1 ? children.get(0) : new AndExpression(children);
    }

    private List<ConditionExpression> parseExprList(List<Element> elements, LoadedSystemConfiguration configuration) {
        List<ConditionExpression> result = new ArrayList<>(elements.size());

        for (Element element : elements) {
            result.add(parseExpr(element, configuration));
        }

        return result;
    }

    private ConditionExpression parseExpr(Element element, LoadedSystemConfiguration configuration) {
        switch (element.getTagName()) {
            case "And":
                return new AndExpression(parseExprList(XmlElements.childElements(element), configuration));
            case "Or":
                return new OrExpression(parseExprList(XmlElements.childElements(element), configuration));
            case "XOr":
                return new XOrExpression(parseExprList(XmlElements.childElements(element), configuration));
            case "Not":
                return new NotExpression(parseExpr(XmlElements.childElements(element).get(0), configuration));
            case "Always":
                return new AlwaysExpression();
            case "Once":
                return new OnceExpression();
            case "Equals":
                return parseComparison(element, ComparisonExpression.Operator.EQUALS, configuration);
            case "NotEquals":
                return parseComparison(element, ComparisonExpression.Operator.NOT_EQUALS, configuration);
            case "GreaterThan":
                return parseComparison(element, ComparisonExpression.Operator.GREATER_THAN, configuration);
            case "LessThan":
                return parseComparison(element, ComparisonExpression.Operator.LESS_THAN, configuration);
            case "GreaterOrEqual":
                return parseComparison(element, ComparisonExpression.Operator.GREATER_OR_EQUAL, configuration);
            case "LessOrEqual":
                return parseComparison(element, ComparisonExpression.Operator.LESS_OR_EQUAL, configuration);
            default:
                throw new ConfigurationException("Unknown condition element: <" + element.getTagName() + ">");
        }
    }

    private ConditionExpression parseComparison(Element element, ComparisonExpression.Operator operator, LoadedSystemConfiguration configuration) {
        TargetAddress targetAddress = TargetAddress.parse(element.getAttribute("target"));
        Class<?> type = configuration.resolveType(targetAddress);
        Supplier<Object> actual = configuration.resolveReader(targetAddress);

        String context = operator + " target=\"" + targetAddress + "\"";
        Supplier<Object> expected = resolveSupplier(element, targetAddress, type, context, configuration);

        return new ComparisonExpression(actual, operator, expected);
    }

    private List<Action> parseActions(Element actionElement, LoadedSystemConfiguration configuration) {
        List<Action> actions = new ArrayList<>();

        for (Element setElement : XmlElements.childElementsNamed(actionElement, "Set")) {
            TargetAddress targetAddress = TargetAddress.parse(setElement.getAttribute("target"));
            Class<?> type = configuration.resolveType(targetAddress);

            String context = "Set target=\"" + targetAddress + "\"";
            Supplier<Object> valueSupplier = resolveSupplier(setElement, targetAddress, type, context, configuration);

            actions.add(new Action(targetAddress, configuration.resolveWriter(targetAddress), valueSupplier));
        }
        return actions;
    }

    private Supplier<Object> resolveSupplier(Element element, TargetAddress targetAddress, Class<?> type, String context, LoadedSystemConfiguration configuration) {
        String value = XmlElements.optionalAttribute(element, "value");
        String ref = XmlElements.optionalAttribute(element, "ref");

        if (type.equals(DeclaredStateValue.class) && value != null && ref == null) {
            DeclaredStateValue literal = configuration.parseDeclaredStateLiteral(targetAddress, value, context);
            return () -> literal;
        }

        ValueSource<?> source = valueSourceParser.parse(value, ref, castParserType(type), context);

        if (source.isReference()) {
            return configuration.resolveReader(source.getReference());
        }

        Object literal = source.getLiteral();
        return () -> literal;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castParserType(Class<?> type) {
        return (Class<T>) type;
    }

    private static boolean parseXsdBoolean(String literal) {
        return "true".equals(literal) || "1".equals(literal);
    }
}