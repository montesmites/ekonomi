package se.montesmites.ekonomi.report.xml;

import java.util.function.Function;

@FunctionalInterface
public interface XmlSectionSupplier {

  XmlSection get(Function<String, XmlSection> sections);
}
