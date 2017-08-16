package se.montesmites.ekonomi.report;

import java.util.Arrays;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.bodyRowsOf;
import static se.montesmites.ekonomi.report.Column.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class TotallingSectionTest {

    private final static String TOTALLING_SECTION_TITLE = "CHECKSUM";

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private Section section1;
    private Section section2;
    private TotallingSection totallingSection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization);
        section1 = new DefaultSection(
                "Section 1",
                () -> bodyRowsOf(
                        fetcher,
                        Arrays.asList(BOKFORT_RESULTAT)));
        section2 = new DefaultSection(
                "Section 2",
                () -> bodyRowsOf(
                        fetcher,
                        Arrays.asList(KORTFRISTIGA_SKULDER)));
        totallingSection = new TotallingSection(
                TOTALLING_SECTION_TITLE,
                Arrays.asList(section1, section2));
    }

    @Test
    public void assertTitle() {
        assertEquals(
                TOTALLING_SECTION_TITLE,
                totallingSection.streamTitle()
                        .findFirst().get().getText(DESCRIPTION));
    }

    @Test
    public void assertNoBodyRows() {
        assertEquals(
                0,
                totallingSection.streamBody().count()
        );
    }

    @Test
    public void assertTotals() {
        Column.streamMonths().forEach(month
                -> assertEquals(
                        month.name(),
                        expectedMonthlyTotal(month),
                        totallingSection.streamFooter()
                                .findFirst().get()
                                .asRowWithAmounts().get()
                                .getMonthlyAmount(month)
                )
        );
    }

    private Currency expectedMonthlyTotal(Column month) {
        return Stream.of(section1, section2)
                .map(section
                        -> section.streamFooter()
                        .findFirst().get()
                        .asRowWithAmounts().get()
                        .getMonthlyAmount(month)
                )
                .reduce(new Currency(0), Currency::add);
    }
}
