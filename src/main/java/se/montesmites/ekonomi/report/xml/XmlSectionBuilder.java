package se.montesmites.ekonomi.report.xml;

import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.report.SectionDecorator;

public class XmlSectionBuilder {

  private final String description;
  private final List<SectionDecorator> decorators;
  private final List<XmlRowBuilder> bodyRowBuilders;

  XmlSectionBuilder(String description) {
    this.description = description;
    this.decorators = new ArrayList<>();
    this.bodyRowBuilders = new ArrayList<>();
  }

  public String getDescription() {
    return description;
  }

  void addBodyRowBuilder(XmlRowBuilder rowBuilder) {
    this.bodyRowBuilders.add(rowBuilder);
  }

  List<XmlRowBuilder> getBodyRowBuilders() {
    return bodyRowBuilders;
  }

  List<SectionDecorator> getDecorators() {
    return decorators;
  }

  void addSectionDecorator(SectionDecorator decorator) {
    this.decorators.add(decorator);
  }
}
