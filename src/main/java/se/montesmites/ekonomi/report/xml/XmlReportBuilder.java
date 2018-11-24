package se.montesmites.ekonomi.report.xml;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.organization.Organization;

public class XmlReportBuilder {

  private final Organization organization;
  private final java.time.Year year;
  private final List<XmlSection> xmlSections;
  private final List<XmlSectionBuilder> sectionBuilders;
  private final String description;

  XmlReportBuilder(
      Organization organization, Year year, String description, List<XmlSection> xmlSections) {
    this.organization = organization;
    this.year = year;
    this.description = description;
    this.xmlSections = xmlSections;
    this.sectionBuilders = new ArrayList<>();
  }

  public Organization getOrganization() {
    return organization;
  }

  public Year getYear() {
    return year;
  }

  void addSectionBuilder(XmlSectionBuilder sectionBuilder) {
    this.sectionBuilders.add(sectionBuilder);
  }

  List<XmlSectionBuilder> getSectionBuilders() {
    return sectionBuilders;
  }

  public String getDescription() {
    return description;
  }

  public List<XmlSection> getXmlSections() {
    return xmlSections;
  }
}
