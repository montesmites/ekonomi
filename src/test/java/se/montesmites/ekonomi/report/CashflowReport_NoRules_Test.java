package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;
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

public class CashflowReport_NoRules_Test {

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
    public void header_texts() {
        HeaderRow header = section.getHeader();
        List<String> expColumnLabels = Arrays.asList("Description", "Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Total");
        List<String> actColumnLabels
                = Column.stream().map(header::getText).collect(toList());
        assertEquals(expColumnLabels, actColumnLabels);
    }

    @Test
    public void body_rowCount() {
        assertEquals(fetcher.streamAccountIds(year).count(),
                section.streamBodyRows().count());
    }

    @Test
    public void body_rowDescription() {
        List<String> exp
                = fetcher.streamAccountIds(year)
                        .map(AccountId::getId)
                        .collect(toList());
        List<String> act
                = section.streamBodyRows()
                        .map(row -> row.getText(Column.DESCRIPTION))
                        .collect(toList());
        assertEquals(exp, act);
    }

    @Test
    public void body_monthlyAmounts() {
        section.streamBodyRows().forEach(bodyRow
                -> Column.streamMonths()
                        .forEach(column
                                -> assertBodyRowMonthlyAmounts(bodyRow, column)));
    }

    @Test
    public void footer_description() {
        FooterRow footer = section.getFooter();
        assertEquals("Total", footer.getText(Column.DESCRIPTION));
    }

    @Test
    public void footer_monthlyTotals() {
        FooterRow footer = section.getFooter();
        Column.streamMonths()
                .forEach(column
                        -> assertFooterRowMonthlyTotal(footer, column));
    }

    private void assertBodyRowMonthlyAmounts(BodyRow row, Column column) {
        Optional<Currency> exp = expectedMonthlyAmount(row, column);
        Optional<Currency> act = row.getMonthlyAmount(column);
        String fmt = "%s %s %s";
        String msg = String.format(fmt, row.getAccountId(), year, column);
        assertEquals(msg, exp, act);
    }

    private void assertFooterRowMonthlyTotal(FooterRow row, Column column) {
        Currency exp = expectedFooterRowMonthlyTotal(column);
        Currency act = row.getMonthlyTotal(column);
        String fmt = "Total %s %s";
        String msg = String.format(fmt, column, year);
        assertEquals(msg, exp, act);
    }

    private Currency expectedFooterRowMonthlyTotal(Column column) {
        return section.streamBodyRows()
                .map(r -> expectedMonthlyAmount(r, column))
                .map(a -> a.orElse(new Currency(0)))
                .reduce((sum, term) -> sum.add(term))
                .get();
    }

    private Optional<Currency> expectedMonthlyAmount(BodyRow row, Column column) {
        return organization.getAccountIdAmountMap(column.asYearMonth(year).get())
                .map(m -> m.get(row.getAccountId()));
    }

    @Test
    public void body_yearlyTotals() {
        section.streamBodyRows().forEach(this::assertBodyRowYearlyTotal);
    }

    @Test
    public void footer_yearlyTotal() {
        FooterRow footer = section.getFooter();
        assertFooterRowYearlyTotal(footer);
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

    private void assertFooterRowYearlyTotal(FooterRow row) {
        Currency exp = Column.streamMonths()
                .map(this::expectedFooterRowMonthlyTotal)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
        Currency act = row.getYearlyTotal();
        assertEquals(exp, act);
    }
}
