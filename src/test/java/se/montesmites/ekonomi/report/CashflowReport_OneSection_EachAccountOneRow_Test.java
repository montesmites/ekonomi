package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
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

public class CashflowReport_OneSection_EachAccountOneRow_Test {

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
        this.fetcher = new CashflowDataFetcher(this.organization, __ -> 1);
        this.report = new CashflowReport(fetcher, year);
        this.section = report.streamSections().findFirst().get();
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    public void header_texts() {
        Row header = section.streamHeader().findFirst().get();
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
        section.streamBodyRows()
                .map(row -> (BodyRow) row)
                .forEach(bodyRow
                        -> Column.streamMonths()
                        .forEach(column
                                -> assertBodyRowMonthlyAmounts(bodyRow, column)));
    }

    @Test
    public void footer_description() {
        Row footer = section.streamFooter().findFirst().get();
        assertEquals("Total", footer.getText(Column.DESCRIPTION));
    }

    @Test
    public void footer_monthlyTotals() {
        Row footer = section.streamFooter().findFirst().get();
        Column.streamMonths()
                .forEach(column
                        -> assertFooterRowMonthlyTotal(footer, column));
    }

    private void assertBodyRowMonthlyAmounts(BodyRow row, Column column) {
        Currency exp = expectedMonthlyAmount(row, column);
        Currency act = row.getMonthlyAmount(column);
        String fmt = "%s %s %s";
        String msg = String.format(fmt, accountId(row), year, column);
        assertEquals(msg, exp, act);
    }

    private void assertFooterRowMonthlyTotal(Row row, Column column) {
        RowWithAmounts rwo = row.asRowWithAmounts().get();
        Currency exp = expectedFooterRowMonthlyTotal(column);
        Currency act = rwo.getMonthlyAmount(column);
        String fmt = "Total %s %s";
        String msg = String.format(fmt, column, year);
        assertEquals(msg, exp, act);
    }

    private Currency expectedFooterRowMonthlyTotal(Column column) {
        return section.streamBodyRows()
                .map(row -> (BodyRow) row)
                .map(r -> expectedMonthlyAmount(r, column))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    private Currency expectedMonthlyAmount(BodyRow row, Column column) {
        return fetcher.getAccountIdAmountMap(column.asYearMonth(year).get())
                .map(m -> m.get(accountId(row))).orElse(new Currency(0));
    }

    @Test
    public void body_yearlyTotals() {
        section.streamBodyRows()
                .map(row -> (BodyRow) row)
                .forEach(this::assertBodyRowYearlyTotal);
    }

    @Test
    public void footer_yearlyTotal() {
        Row footer = section.streamFooter().findFirst().get();
        assertFooterRowYearlyTotal(footer);
    }

    private void assertBodyRowYearlyTotal(BodyRow row) {
        Currency exp = organization.streamEntries()
                .filter(e -> e.getAccountId().equals(accountId(row)))
                .map(e -> e.getAmount())
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
        Currency act = row.getYearlyTotal();
        String fmt = "%s";
        String msg = String.format(fmt, accountId(row));
        assertEquals(msg, exp, act);
    }

    private void assertFooterRowYearlyTotal(Row row) {
        RowWithAmounts rwa = row.asRowWithAmounts().get();
        Currency exp = Column.streamMonths()
                .map(this::expectedFooterRowMonthlyTotal)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
        Currency act = rwa.getYearlyTotal();
        assertEquals(exp, act);
    }

    private AccountId accountId(BodyRow row) {
        return row.getAccountIds().get().findFirst().get();
    }
}
