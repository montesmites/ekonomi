package se.montesmites.ekonomi.report.xml;

import java.util.List;

class _ReportDefinition4Test {

  private final String description;
  private final List<_SectionDefinition4Test> sections;

  _ReportDefinition4Test(String description, List<_SectionDefinition4Test> sections) {
    this.description = description;
    this.sections = sections;
  }

  public String getDescription() {
    return description;
  }

  List<_SectionDefinition4Test> getSections() {
    return sections;
  }
}
