package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_NoTemplate_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private Section section;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year);
        this.section = report.streamSections().findFirst().get();
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    public void columnLabels() {
        List<String> expColumnLabels = Arrays.asList("Description", "Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Total");
        List<String> actColumnLabels = report.streamColumns().map(
                Column::getLabel).collect(toList());
        assertEquals(expColumnLabels, actColumnLabels);
    }
    
    @Test
    public void header_description() {
        HeaderRow header = section.getHeader();
        assertEquals("Description", header.getDescription());
    }
    
    @Test
    public void footer_description() {
        FooterRow footer = section.getFooter();
        assertEquals("Total", footer.getDescription());
    }
    
    @Test
    public void body_rowCount() {
        assertEquals(fetcher.streamAccountIds(year).count(),
                section.streamBodyRows().count());
    }

    @Test
    public void body_rowLabels() {
        List<String> exp
                = fetcher.streamAccountIds(year)
                        .map(AccountId::getId)
                        .collect(toList());
        List<String> act
                = section.streamBodyRows()
                        .map(Row::getDescription)
                        .collect(toList());
        assertEquals(exp, act);
    }

    @Test
    public void body_monthlyAmounts() {
        section.streamBodyRows().forEach(bodyRow
                -> yearMonths().forEach(yearMonth
                        -> assertBodyRowMonthlyAmonuts(bodyRow, yearMonth)));
    }

    private void assertBodyRowMonthlyAmonuts(BodyRow row, YearMonth yearMonth) {
        Optional<Currency> exp
                = organization.getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(row.getAccountId()));
        Optional<Currency> act
                = row.getMonthlyAmount(yearMonth);
        String fmt = "%s (%s)";
        String msg = String.format(fmt, row.getAccountId(), yearMonth);
        assertEquals(msg, exp, act);
    }

    @Test
    public void body_yearlyTotals() {
        section.streamBodyRows().forEach(this::assertBodyRowYearlyTotal);
    }

    private void assertBodyRowYearlyTotal(BodyRow row) {
        Currency exp = organization.streamEntries()
                .filter(e -> e.getAccountId().equals(row.getAccountId()))
                .map(e -> e.getAmount())
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
        Currency act = row.getYearlyTotal();
        String fmt = "%s";
        String msg = String.format(fmt, row.getAccountId());
        assertEquals(msg, exp, act);
    }

    private Stream<YearMonth> yearMonths() {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }
}
