package se.montesmites.ekonomi.report.xml;

import java.util.ArrayList;
import java.util.List;

public class XmlSectionBuilder {

  private final String description;
  private final List<XmlRowBuilder> bodyRowBuilders;

  XmlSectionBuilder(String description) {
    this.description = description;
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
}
