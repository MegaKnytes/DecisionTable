package org.megaknytes.decisiontable.core.parse;

import org.megaknytes.decisiontable.core.DecisionTable;
import org.megaknytes.decisiontable.core.SystemConfiguration;
import org.megaknytes.decisiontable.core.rule.value.snapshot.Snapshot;
import org.megaknytes.decisiontable.core.rule.value.snapshot.SnapshotValue;
import org.megaknytes.decisiontable.core.rule.address.Address;
import org.megaknytes.decisiontable.core.rule.Rule;
import org.megaknytes.decisiontable.core.rule.Action;
import org.megaknytes.decisiontable.core.rule.expression.logical.AlwaysExpression;
import org.megaknytes.decisiontable.core.rule.expression.logical.AndExpression;
import org.megaknytes.decisiontable.core.rule.expression.comparison.CachingComparison;
import org.megaknytes.decisiontable.core.rule.expression.comparison.ComparisonExpression;
import org.megaknytes.decisiontable.core.rule.expression.comparison.Constant;
import org.megaknytes.decisiontable.core.rule.expression.ConditionExpression;
import org.megaknytes.decisiontable.core.rule.expression.logical.NotExpression;
import org.megaknytes.decisiontable.core.rule.expression.logical.OnceExpression;
import org.megaknytes.decisiontable.core.rule.expression.logical.OrExpression;
import org.megaknytes.decisiontable.core.rule.expression.logical.XOrExpression;
import org.megaknytes.decisiontable.core.rule.value.type.State;
import org.megaknytes.decisiontable.core.rule.value.ValueParserRegistry;
import org.megaknytes.decisiontable.core.rule.value.Value;
import org.megaknytes.decisiontable.core.rule.value.ValueParser;
import org.megaknytes.decisiontable.core.utils.exception.XmlParseException;
import org.megaknytes.decisiontable.core.utils.xml.XmlElements;
import org.megaknytes.decisiontable.core.utils.exception.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Responsible for loading a file containing Decision Table XML into a Decision Table object
 */
public class DecisionTableParser {
    private final ValueParser valueParser;

    public DecisionTableParser(ValueParserRegistry valueParserRegistry) {
        this.valueParser = new ValueParser(valueParserRegistry);
    }

    /**
     * Given a file containing Decision Table XML and its System Configuration, attempt to load rules
     *
     * @param file the file that contains the Decision Table XML
     * @param configuration the System Configuration as named in the Decision Table
     * @return a Decision Table object matching the given XML file
     */
    public DecisionTable load(File file, SystemConfiguration configuration) {
        Document document;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);

            document.getDocumentElement().normalize();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new XmlParseException("Failed to parse " + file.getName() + ": " + e.getMessage());
        }

        Element root = document.getDocumentElement();
        String name = root.getAttribute("name");

        ParsingConfiguration parsingConfiguration = new ParsingConfiguration(configuration);

        List<Rule> rules = new ArrayList<>();

        for (Element rulesSection : XmlElements.childElementsNamed(root, "Rules")) {
            for (Element child : XmlElements.childElements(rulesSection)) {
                // Iterate over the RuleGroup to discover each nested rule.
                if (child.getTagName().equals("RuleGroup")) {
                    String groupName = child.getAttribute("name");
                    String enabledAttribute = XmlElements.optionalAttribute(child, "enabled");
                    boolean groupEnabled = enabledAttribute == null || parseBoolean(enabledAttribute);

                    if (!groupEnabled) {
                        continue;
                    }

                    for (Element ruleElement : XmlElements.childElementsNamed(child, "Rule")) {
                        rules.add(parseRule(ruleElement.getAttribute("name"), groupName, ruleElement, parsingConfiguration));
                    }
                } else {
                    rules.add(parseRule(child.getAttribute("name"), null, child, parsingConfiguration));
                }
            }
        }

        return new DecisionTable(name, rules, parsingConfiguration.snapshot);
    }

    /**
     * Parses a Rule XML element into a Rule object, including its condition and actions
     *
     * @param ruleName the rule's name attribute
     * @param groupName the RuleGroup name, or null if the rule is not grouped
     * @param ruleElement the Rule XML element to parse
     * @param context the parsing context
     * @return the parsed Rule, including its condition and actions
     */
    private Rule parseRule(String ruleName, String groupName, Element ruleElement, ParsingConfiguration context) {
        String priorityAttribute = XmlElements.optionalAttribute(ruleElement, "priority");
        int priority = priorityAttribute != null ? Integer.parseInt(priorityAttribute) : 0;

        Element conditionElement = XmlElements.childElementsNamed(ruleElement, "Condition").get(0);
        Element actionElement = XmlElements.childElementsNamed(ruleElement, "Action").get(0);

        List<ConditionExpression> conditionChildren = parseExpressionList(XmlElements.childElements(conditionElement), context);
        // If multiple conditions exist and aren't wrapped in an AndExpression, do so for the user
        ConditionExpression condition = conditionChildren.size() == 1 ? conditionChildren.get(0) : new AndExpression(conditionChildren);

        List<Action> actions = new ArrayList<>();

        for (Element setElement : XmlElements.childElementsNamed(actionElement, "Set")) {
            Address address = Address.parse(setElement.getAttribute("target"));
            Class<?> type = context.configuration.getAddressType(address);

            String errorContext = "Set target=\"" + address + "\"";
            Supplier<Object> valueSupplier = getSupplier(setElement, address, type, errorContext, context);

            actions.add(new Action(address, context.configuration.getWriter(address), valueSupplier));
        }

        return new Rule(ruleName, groupName, priority, condition, actions);
    }

    /**
     * Parses a Condition XML element into a ConditionExpression
     *
     * @param element the XML element to parse
     * @param context the parsing context
     * @return the ConditionExpression
     */
    private ConditionExpression parseExpression(Element element, ParsingConfiguration context) {
        switch (element.getTagName()) {
            // These cases are nestable, and therefore are recursive
            case "And":
                return new AndExpression(parseExpressionList(XmlElements.childElements(element), context));
            case "Or":
                return new OrExpression(parseExpressionList(XmlElements.childElements(element), context));
            case "XOr":
                return new XOrExpression(parseExpressionList(XmlElements.childElements(element), context));
            case "Not":
                return new NotExpression(parseExpression(XmlElements.childElements(element).get(0), context));
            // Useful for actions that always need to run (ex. drivetrain) or only need to run once (ex. initialization)
            case "Always":
                return new AlwaysExpression();
            case "Once":
                return new OnceExpression();
            // Comparison between objects
            case "Equals":
                return parseComparison(element, ComparisonExpression.Operator.EQUALS, context);
            case "NotEquals":
                return parseComparison(element, ComparisonExpression.Operator.NOT_EQUALS, context);
            case "GreaterThan":
                return parseComparison(element, ComparisonExpression.Operator.GREATER_THAN, context);
            case "LessThan":
                return parseComparison(element, ComparisonExpression.Operator.LESS_THAN, context);
            case "GreaterOrEqual":
                return parseComparison(element, ComparisonExpression.Operator.GREATER_OR_EQUAL, context);
            case "LessOrEqual":
                return parseComparison(element, ComparisonExpression.Operator.LESS_OR_EQUAL, context);
            default:
                throw new ConfigurationException("Unknown condition : <" + element.getTagName() + ">, check your file.");
        }
    }

    private List<ConditionExpression> parseExpressionList(List<Element> elements, ParsingConfiguration context) {
        List<ConditionExpression> result = new ArrayList<>();

        for (Element element : elements) {
            result.add(parseExpression(element, context));
        }

        return result;
    }

    /**
     * Parses a Comparison XML element into a CachingComparison
     *
     * @param element the comparison XML element with a target and a value or reference
     * @param operator the comparison operator
     * @param context the parsing context
     * @return a CachingComparison
     */
    private CachingComparison parseComparison(Element element, ComparisonExpression.Operator operator, ParsingConfiguration context) {
        Address address = Address.parse(element.getAttribute("target"));
        Class<?> type = context.configuration.getAddressType(address);
        SnapshotValue actual = valueOf(address, context);

        String errorContext = operator + " target=\"" + address + "\"";
        Supplier<Object> expected = getSupplier(element, address, type, errorContext, context);

        return context.getOrCreateCachingComparison(actual, operator, expected);
    }

    private Supplier<Object> getSupplier(Element element, Address address, Class<?> type, String errorContext, ParsingConfiguration context) {
        String value = XmlElements.optionalAttribute(element, "value");
        String ref = XmlElements.optionalAttribute(element, "ref");

        if (type.equals(State.class) && value != null && ref == null) {
            State state = context.configuration.getDeclaredState(address, value, errorContext);
            return new Constant(state);
        }

        Value<?> source = valueParser.parse(value, element, ref, castToType(type), errorContext);

        if (source.getReferenceAddress()) {
            return valueOf(source.getReference(), context);
        }

        return new Constant(source.getLiteral());
    }

    private SnapshotValue valueOf(Address address, ParsingConfiguration context) {
        return context.snapshot.valueOf(address, context.configuration.getReader(address));
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castToType(Class<?> type) {
        return (Class<T>) type;
    }

    private static boolean parseBoolean(String literal) {
        return "true".equals(literal) || "1".equals(literal);
    }

    private static final class ParsingConfiguration {
        private final SystemConfiguration configuration;
        private final Snapshot snapshot = new Snapshot();
        private final Map<CachingComparisonIdentity, CachingComparison> comparisons = new HashMap<>();

        ParsingConfiguration(SystemConfiguration configuration) {
            this.configuration = configuration;
        }

        // Avoid creating duplicate CachingComparison objects for the same SnapshotValue, operator, and expected value by keeping track of each created cache.
        CachingComparison getOrCreateCachingComparison(SnapshotValue current, ComparisonExpression.Operator operator, Supplier<Object> expected) {
            return comparisons.computeIfAbsent(new CachingComparisonIdentity(current, operator, expected instanceof SnapshotValue ? expected : expected.get()),
                    k -> new CachingComparison(new ComparisonExpression(current, operator, expected), snapshot));
        }
    }

    private static final class CachingComparisonIdentity {
        private final SnapshotValue value;
        private final ComparisonExpression.Operator operator;
        private final Object expected;

        CachingComparisonIdentity(SnapshotValue value, ComparisonExpression.Operator operator, Object expectedValue) {
            this.value = value;
            this.operator = operator;
            this.expected = expectedValue;
        }

        @Override
        public boolean equals(Object comparisonObject) {
            // Direct Equivalence
            if (this == comparisonObject) {
                return true;
            // Type Equivalence
            } else if (!(comparisonObject instanceof CachingComparisonIdentity)) {
                return false;
            }

            // Cast to CachingComparisonKey for field comparison
            CachingComparisonIdentity castedComparison = (CachingComparisonIdentity) comparisonObject;

            // Compare the fields for equality
            return this.value == castedComparison.value && this.operator == castedComparison.operator && Objects.equals(this.expected, castedComparison.expected);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(value), operator, expected);
        }
    }
}