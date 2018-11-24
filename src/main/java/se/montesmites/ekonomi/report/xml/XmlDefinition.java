package se.montesmites.ekonomi.report.xml;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.CashflowDataFetcher;

@XmlRootElement(name = "definition")
class XmlDefinition {

  private XmlReport report;
  private List<XmlSection> sections;
  private List<XmlAccountGroup> accountGroups;

  public XmlReport getReport() {
    return report;
  }

  public void setReport(XmlReport report) {
    this.report = report;
  }

  @XmlElement(name = "section")
  List<XmlSection> getSections() {
    if (sections == null) {
      this.sections = new ArrayList<>();
    }
    return this.sections;
  }

  public void setSections(List<XmlSection> sections) {
    this.sections = sections;
  }

  @XmlElement(name = "account-group")
  private List<XmlAccountGroup> getAccountGroups() {
    if (accountGroups == null) {
      this.accountGroups = new ArrayList<>();
    }
    return this.accountGroups;
  }

  public void setAccountGroups(List<XmlAccountGroup> accountGroups) {
    this.accountGroups = accountGroups;
  }

  XmlReportBuilder toReportBuilder(Organization organization, java.time.Year year) {
    var sectionsMap = this.getSections().stream().collect(toMap(XmlSection::getId, identity()));
    var accountGroupsMap =
        this.getAccountGroups().stream().collect(toMap(XmlAccountGroup::getId, identity()));
    var fetcher = new CashflowDataFetcher(organization);
    var builder =
        new XmlReportBuilder(organization, year, report.getDescription(), this.getSections());
    var sections =
        report.getSectionSuppliers().stream().map(supplier -> supplier.get(sectionsMap::get));
    var sectionBuilders =
        sections.map(section -> section.toSectionBuilder(fetcher, year, accountGroupsMap::get));
    sectionBuilders.forEach(builder::addSectionBuilder);
    return builder;
  }
}
