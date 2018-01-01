package se.montesmites.ekonomi.report.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.ReportBuilder;
import se.montesmites.ekonomi.report.RowBuilder;
import se.montesmites.ekonomi.report.SectionBuilder;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import javax.xml.bind.JAXB;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DefaultTestDataExtension.class)
class DefinitionTest {
    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;

    private XmlDefinition xmlDefinition;
    private ReportBuilder reportBuilder;

    @Test
    void t01_oneSectionOneRowSimpleDefinition() {
        final String path = "/se/montesmites/ekonomi/report/xml/01_one-section-one-row-simple-definition.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        assertAll("report",
                  () -> assertEquals(1, getSectionBuilders().size()),
                  () -> assertEquals("Section 1", getSectionBuilderAt(0).getDescription()),
                  () -> assertEquals(1, getBodyRowBuildersAt(0).size()),
                  () -> assertEquals("Account Group 1", getBodyRowBuilderAt(0, 0).getDescription()),
                  () -> assertEquals("Account Group 1", getRegexPatternAt(0,0))
        );
    }

    @Test
    void t02_oneSectionTwoRowsSimpleDefinition() {
        final String path = "/se/montesmites/ekonomi/report/xml/02_one-section-two-rows-simple-definition.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        assertAll("report",
                  () -> assertEquals(1, getSectionBuilders().size()),
                  () -> assertEquals("Section 1", getSectionBuilderAt(0).getDescription()),
                  () -> assertEquals(2, getBodyRowBuildersAt(0).size()),
                  () -> assertEquals("Account Group 1", getBodyRowBuilderAt(0, 0).getDescription()),
                  () -> assertEquals("Account Group 1", getRegexPatternAt(0, 0)),
                  () -> assertEquals("Account Group 2", getBodyRowBuilderAt(0, 1).getDescription()),
                  () -> assertEquals("Account Group 2", getRegexPatternAt(0, 1))
        );
    }

    private List<SectionBuilder> getSectionBuilders() {
        return reportBuilder.getSectionBuilders();
    }

    private SectionBuilder getSectionBuilderAt(int section) {
        return getSectionBuilders().get(section);
    }

    private List<RowBuilder> getBodyRowBuildersAt(int section) {
        return getSectionBuilderAt(section).getBodyRowBuilders();
    }

    private RowBuilder getBodyRowBuilderAt(int section, int row) {
        return getBodyRowBuildersAt(section).get(row);
    }

    private String getRegexPatternAt(int section, int row) {
        AccountFilterByRegex filter = (AccountFilterByRegex) getBodyRowBuilderAt(section, row).getFilter();
        return filter.getPattern().pattern();
    }
}
