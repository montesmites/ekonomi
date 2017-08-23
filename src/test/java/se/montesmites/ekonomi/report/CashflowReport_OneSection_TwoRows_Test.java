package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.*;
import static se.montesmites.ekonomi.report.Column.*;

import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_OneSection_TwoRows_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final static String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private Section section;
    private List<CashflowReport_AccountGroup_2012> groups;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = new OrganizationBuilder(tempfolder.getRoot().toPath()).build();
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year, () -> sections());
        this.groups = Arrays.asList(BOKFORT_RESULTAT, KORTFRISTIGA_SKULDER);
        this.section = new DefaultSection(
                DEN_LOPANDE_VERKSAMHETEN,
                () -> bodyRowsOf(fetcher, groups));
    }

    private Stream<Section> sections() {
        return Stream.of(section);
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    public void sectionTitle() {
        final String exp = DEN_LOPANDE_VERKSAMHETEN.toUpperCase();
        final String act = section.streamTitle()
                .findFirst().get().formatText(DESCRIPTION);
        assertEquals(exp, act);
    }

    @Test
    public void body_rowDescription() {
        assertBodyRowDescriptions(section, groups);
    }

    @Test
    public void testMonthlyAmountsForGroupedRow() {
        assertMonthlyAmounts(section, groups);
    }
}
