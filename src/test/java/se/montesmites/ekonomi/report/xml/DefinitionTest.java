package se.montesmites.ekonomi.report.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.ReportBuilder;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import javax.xml.bind.JAXB;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DefaultTestDataExtension.class)
class DefinitionTest {
    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;

    @Test
    void t01_oneSectionOneRowSimpleDefinition() {
        final String path = "/se/montesmites/ekonomi/report/xml/01_one-section-one-row-simple-definition.xml";
        final XmlDefinition definition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        final ReportBuilder reportBuilder = definition.toReportBuilder(organization, year);
        assertAll("report",
                  () -> assertEquals(1, reportBuilder.getSectionBuilders().size()),
                  () -> assertEquals("Section 1", reportBuilder.getSectionBuilders().get(0).getDescription()),
                  () -> assertEquals(1, reportBuilder.getSectionBuilders().get(0).getBodyRowBuilders().size()),
                  () -> assertEquals("Account Group 1", reportBuilder.getSectionBuilders().get(0).getBodyRowBuilders().get(0).getDescription()),
                  () -> assertEquals(AccountFilterByRegex.class, reportBuilder.getSectionBuilders().get(0).getBodyRowBuilders().get(0).getFilter().getClass()),
                  () -> assertEquals("Account Group 1", ((AccountFilterByRegex) reportBuilder.getSectionBuilders().get(0).getBodyRowBuilders().get(0).getFilter()).getPattern().pattern())
        );
    }
}
