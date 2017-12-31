package se.montesmites.ekonomi.report.xml;

import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.ReportBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "definition")
class XmlDefinition {
    private XmlReport report;

    public XmlReport getReport() {
        return report;
    }

    public void setReport(XmlReport report) {
        this.report = report;
    }

    public ReportBuilder toReportBuilder(Organization organization, java.time.Year year) {
        final CashflowDataFetcher fetcher = new CashflowDataFetcher(organization);
        final ReportBuilder builder = new ReportBuilder(organization, year);
        report.getSections().stream().map(section -> section.toSectionBuilder(fetcher, year)).forEach(builder::addSectionBuilder);
        return builder;
    }
}
