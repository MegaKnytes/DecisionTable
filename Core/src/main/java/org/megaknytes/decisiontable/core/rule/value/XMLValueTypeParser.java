package org.megaknytes.decisiontable.core.rule.value;

import org.w3c.dom.Element;

public interface XMLValueTypeParser<T> extends ValueTypeParser<T> {
    T parse(Element element);
}