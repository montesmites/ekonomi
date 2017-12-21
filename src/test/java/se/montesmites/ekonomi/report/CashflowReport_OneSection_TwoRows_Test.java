package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.*;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

@ExtendWith(DefaultTestDataExtension.class)
class CashflowReport_OneSection_TwoRows_Test {
    private final static String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";

    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private Section section;
    private List<CashflowReport_AccountGroup_2012> groups;

    @BeforeEach
    void before() {
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year, this::sections);
        this.groups = Arrays.asList(BOKFORT_RESULTAT, KORTFRISTIGA_SKULDER);
        this.section = new DefaultSection(
                DEN_LOPANDE_VERKSAMHETEN,
                () -> bodyRowsOf(fetcher, groups));
    }

    private Stream<Section> sections() {
        return Stream.of(section);
    }

    @Test
    void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    void sectionTitle() {
        final String exp = DEN_LOPANDE_VERKSAMHETEN.toUpperCase();
        final String act = section.streamTitle()
                .findFirst().get().formatText(DESCRIPTION);
        assertEquals(exp, act);
    }

    @Test
    void body_rowDescription() {
        assertBodyRowDescriptions(section, groups);
    }

    @Test
    void testMonthlyAmountsForGroupedRow() {
        assertMonthlyAmounts(section, groups);
    }
}
