package se.montesmites.ekonomi.report.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.report.*;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import javax.xml.bind.JAXB;
import java.time.Year;
import java.util.List;

import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.xml._Definition4TestUtil.*;

@ExtendWith(DefaultTestDataExtension.class)
class DefinitionTest {
    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;

    private XmlDefinition xmlDefinition;
    private ReportBuilder reportBuilder;

    @Test
    void t01_oneSectionOneRowSimpleDefinition() {
        var path = "/se/montesmites/ekonomi/report/xml/01_one-section-one-row-simple-definition.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("One Section One Row Simple Definition",
                           of(
                                   section("Section 1",
                                           of(
                                                   row("Description 1-1", "Regex 1-1")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    @Test
    void t02_oneSectionTwoRowsSimpleDefinition() {
        var path = "/se/montesmites/ekonomi/report/xml/02_one-section-two-rows-simple-definition.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("One Section Two Rows Simple Definition",
                           of(
                                   section("Section 1",
                                           of(
                                                   row("Description 1-1", "Regex 1-1"),
                                                   row("Description 1-2", "Regex 1-2")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    @Test
    void t03_twoSectionsOneRowEachSimpleDefinition() {
        var path = "/se/montesmites/ekonomi/report/xml/03_two-sections-one-row-each-simple-definition.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("Two Sections One Row Each Simple Definition",
                           of(
                                   section("Section 1",
                                           of(
                                                   row("Description 1-1", "Regex 1-1")
                                           )
                                   ),
                                   section("Section 2",
                                           of(
                                                   row("Description 2-1", "Regex 2-1")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    @Test
    void t04_reportWithTwoSectionRefs() {
        var path = "/se/montesmites/ekonomi/report/xml/04_report-with-two-sectionrefs.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("Report with Two SectionRefs",
                           of(
                                   section("Section 1",
                                           of(
                                                   row("Description 1-1", "Regex 1-1")
                                           )
                                   ),
                                   section("Section 2",
                                           of(
                                                   row("Description 2-1", "Regex 2-1")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    @Test
    void t05_sectionWithAccountGroupRef() {
        var path = "/se/montesmites/ekonomi/report/xml/05_section-with-accountgroupref.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("Section with AccountGroupRef",
                           of(
                                   section("Section 1",
                                           of(
                                                   row("Description 1-1", "Regex 1-1")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    @Test
    void t06_twoSectionRefsOneCompact() {
        var path = "/se/montesmites/ekonomi/report/xml/06_two-sectionrefs-one-compact-ref-one-compact-section.xml";
        this.xmlDefinition = JAXB.unmarshal(getClass().getResourceAsStream(path), XmlDefinition.class);
        this.reportBuilder = xmlDefinition.toReportBuilder(organization, year);
        var definition =
                definition("Two SectionRefs, One Compact Ref, One Compact Section",
                           of(
                                   section("Section 1", of(CompactSectionDecorator.class),
                                           of(
                                                   row("Description 1-1", "Regex 1-1")
                                           )
                                   ),
                                   section("Section 2", of(CompactSectionDecorator.class),
                                           of(
                                                   row("Description 2-1", "Regex 2-1")
                                           )
                                   )
                           )
                );
        doAssert(definition);
    }

    private void doAssert(_ReportDefinition4Test definition) {
        assertEquals(definition.getDescription(), reportBuilder.getDescription(), "definition description");
        assertEquals(definition.getSections().size(), getSectionBuilders().size(), "section count");
        var sectionIndex = 0;
        for (var section : definition.getSections()) {
            var sectionMessage = "section " + sectionIndex + 1;
            assertEquals(section.getDescription(), getSectionBuilderAt(sectionIndex).getDescription(), sectionMessage + ", description");
            assertEquals(section.getRows().size(), getBodyRowBuildersAt(sectionIndex).size(), sectionMessage + ", row count");
            assertEquals(section.getDecorators(), getSectionBuilderAt(sectionIndex).getDecorators().stream().map(SectionDecorator::getClass).collect(toList()));
            var rowIndex = 0;
            for (var row : section.getRows()) {
                var rowMessage = sectionMessage + ", row " + row + 1;
                assertEquals(row.getDescription(), getBodyRowBuilderAt(sectionIndex, rowIndex).getDescription(), rowMessage + ", description");
                assertEquals(row.getRegex(), getRegexPatternAt(sectionIndex, rowIndex), rowMessage + ", regex pattern");
                rowIndex++;
            }
            sectionIndex++;
        }
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
        var filter = (AccountFilterByRegex) getBodyRowBuilderAt(section, row).getFilter();
        return filter.getPattern().pattern();
    }
}
