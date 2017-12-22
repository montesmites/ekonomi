package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import java.time.Year;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.*;

@ExtendWith(DefaultTestDataExtension.class)
class CashflowReport_TwoSections_OneRowEach_Test {

    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;

    @BeforeEach
    void before() {
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year, () -> sections().stream().map(Map.Entry::getKey));
    }

    private List<Map.Entry<Section, List<CashflowReport_AccountGroup_2012>>> sections() {
        return List.of(section("Bokfört resultat", List.of(BOKFORT_RESULTAT)), section("Kortfristiga skulder", List.of(KORTFRISTIGA_SKULDER)));
    }

    private Map.Entry<Section, List<CashflowReport_AccountGroup_2012>> section(String title, List<CashflowReport_AccountGroup_2012> groups) {
        return new SimpleEntry<>(new DefaultSection(title, () -> bodyRowsOf(fetcher, groups)), groups);
    }

    @Test
    void sectionCount() {
        assertEquals(sections().size(), report.streamSections().count());
    }

    @Test
    void body_rowDescription() {
        sections().forEach(section -> assertBodyRowDescriptions(section.getKey(), section.getValue()));
    }

    @Test
    void testMonthlyAmountsForGroupedRow() {
        sections().forEach(section -> assertMonthlyAmounts(section.getKey(), section.getValue()));
    }

    @Test
    void testAverage() {
        sections().forEach(section -> assertExpectedAverages(section.getKey(), section.getValue()));
    }
}
