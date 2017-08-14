package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertBodyRowDescriptions;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertMonthlyAmounts;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.bodyRowsOf;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_TwoSections_OneRowEach_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization, __ -> 1);
        this.report = new CashflowReport(
                fetcher,
                year,
                () -> sections().stream().map(e -> e.getKey()));
    }

    private List<Map.Entry<Section, List<CashflowReport_AccountGroup_2012>>> sections() {
        return Arrays.asList(
                section(
                        "Section 1",
                        Arrays.asList(BOKFORT_RESULTAT)),
                section(
                        "Section 2",
                        Arrays.asList(KORTFRISTIGA_SKULDER)));
    }

    private Map.Entry<Section, List<CashflowReport_AccountGroup_2012>> section(
            String title, List<CashflowReport_AccountGroup_2012> groups) {
        return new SimpleEntry<>(
                new Section(
                        title,
                        year,
                        () -> bodyRowsOf(
                                fetcher,
                                groups)),
                groups);
    }

    @Test
    public void sectionCount() {
        assertEquals(sections().size(), report.streamSections().count());
    }

    @Test
    public void body_rowDescription() {
        sections().stream().forEach(section
                -> assertBodyRowDescriptions(
                        section.getKey(),
                        section.getValue()));
    }

    @Test
    public void testMonthlyAmountsForGroupedRow() {
        sections().stream().forEach(section
                -> assertMonthlyAmounts(
                        section.getKey(),
                        section.getValue()));
    }
}
