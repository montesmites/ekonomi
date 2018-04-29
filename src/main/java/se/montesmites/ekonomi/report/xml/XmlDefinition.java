package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.ReportBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@XmlRootElement(name = "definition")
class XmlDefinition {
    private XmlReport report;
    private List<XmlSection> sections;

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

    ReportBuilder toReportBuilder(Organization organization, java.time.Year year) {
        var fetcher = new CashflowDataFetcher(organization);
        var builder = new ReportBuilder(organization, year, report.getDescription(), this.getSections());
        var sectionsMap = this.getSections().stream().collect(toMap(XmlSection::getId, identity()));
        var sections = report.getSectionSuppliers().stream().map(supplier -> supplier.get(sectionsMap::get));
        var sectionBuilders = sections.map(section -> section.toSectionBuilder(fetcher, year));
        sectionBuilders.forEach(builder::addSectionBuilder);
        return builder;
    }
}
