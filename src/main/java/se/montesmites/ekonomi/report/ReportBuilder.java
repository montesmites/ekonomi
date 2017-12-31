package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.organization.Organization;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {
    private final Organization organization;
    private final java.time.Year year;
    private final List<SectionBuilder> sectionBuilders;

    public ReportBuilder(Organization organization, Year year) {
        this.organization = organization;
        this.year = year;
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
}
