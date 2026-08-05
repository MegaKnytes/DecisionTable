package org.megaknytes.decisiontable.core.rules.value;

import java.util.Collections;
import java.util.List;

public interface ValueParser<T> {
    T parse(String literal);

    Class<T> getType();

    default List<String> getPossibleValues() {
        return Collections.emptyList();
    }
}