package org.megaknytes.decisiontable.core.rule.value;

public interface ValueTypeParser<T> {
    T parse(String literal);

    Class<T> getType();
}