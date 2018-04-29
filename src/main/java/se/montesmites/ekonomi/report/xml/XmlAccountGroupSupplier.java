package se.montesmites.ekonomi.report.xml;

import java.util.function.Function;

@FunctionalInterface
public interface XmlAccountGroupSupplier {
    XmlAccountGroup get(Function<String, XmlAccountGroup> accountGroups);
}
