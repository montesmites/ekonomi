package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.xml.XmlSection;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {
    private final Organization organization;
    private final java.time.Year year;
    private final List<XmlSection> xmlSections;
    private final List<SectionBuilder> sectionBuilders;
    private final String description;

    public ReportBuilder(Organization organization, Year year, String description, List<XmlSection> xmlSections) {
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

    public void addSectionBuilder(SectionBuilder sectionBuilder) {
        this.sectionBuilders.add(sectionBuilder);
    }

    public List<SectionBuilder> getSectionBuilders() {
        return sectionBuilders;
    }

    public String getDescription() {
        return description;
    }

    public List<XmlSection> getXmlSections() {
        return xmlSections;
    }
}
