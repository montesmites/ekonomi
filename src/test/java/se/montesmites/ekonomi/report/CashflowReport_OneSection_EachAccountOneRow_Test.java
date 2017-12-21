package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import java.time.Year;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static se.montesmites.ekonomi.report.Signedness.NEGATED_SIGN;

@ExtendWith(DefaultTestDataExtension.class)
class CashflowReport_OneSection_EachAccountOneRow_Test {
    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private Section section;

    @BeforeEach
    void before() {
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year);
        this.section = report.streamSections().findFirst().get();
    }

    @Test
    void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    void body_rowCount() {
        assertEquals(fetcher.streamAccountIds(year).count(),
                section.streamBody().count());
    }

    @Test
    void body_rowDescription() {
        List<String> exp
                = fetcher.streamAccountIds(year)
                        .map(AccountId::getId)
                        .collect(toList());
        List<String> act
                = section.streamBody()
                        .map(row -> row.formatText(Column.DESCRIPTION))
                        .collect(toList());
        assertEquals(exp, act);
    }

    @Test
    void body_monthlyAmounts() {
        section.streamBody()
                .map(row -> (RowWithAccounts) row)
                .forEach(bodyRow
                        -> Column.streamMonths()
                        .forEach(column
                                -> assertBodyRowMonthlyAmounts(bodyRow, column)));
    }

    @Test
    void footer_monthlyTotals() {
        Row footer = section.streamFooter().findFirst().get();
        Column.streamMonths()
                .forEach(column
                        -> assertFooterRowMonthlyTotal(footer, column));
    }

    private void assertBodyRowMonthlyAmounts(RowWithAccounts row, Column column) {
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
        final Currency amount = section.streamBody()
                .map(row -> (RowWithAccounts) row)
                .map(r -> expectedMonthlyAmount(r, column))
                .reduce(new Currency(0), Currency::add);
        return NEGATED_SIGN.apply(amount);
    }

    private Currency expectedMonthlyAmount(RowWithAccounts row, Column column) {
        final Currency amount = fetcher.getAccountIdAmountMap(
                column.asYearMonth(year).get())
                .map(m -> m.get(accountId(row))).orElse(new Currency(0));
        return NEGATED_SIGN.apply(amount);
    }

    @Test
    void body_yearlyTotals() {
        section.streamBody()
                .map(row -> (RowWithAccounts) row)
                .forEach(this::assertBodyRowYearlyTotal);
    }

    @Test
    void footer_yearlyTotal() {
        Row footer = section.streamFooter().findFirst().get();
        assertFooterRowYearlyTotal(footer);
    }

    private void assertBodyRowYearlyTotal(RowWithAccounts row) {
        Currency exp = NEGATED_SIGN.apply(
                organization.streamEntries()
                        .filter(e -> e.getAccountId().equals(accountId(row)))
                        .map(Entry::getAmount)
                        .reduce(new Currency(0), Currency::add));
        Currency act = row.getYearlyTotal();
        String fmt = "%s";
        String msg = String.format(fmt, accountId(row));
        assertEquals(msg, exp, act);
    }

    private void assertFooterRowYearlyTotal(Row row) {
        RowWithAmounts rwa = row.asRowWithAmounts().get();
        Currency exp = Column.streamMonths()
                .map(this::expectedFooterRowMonthlyTotal)
                .reduce(new Currency(0), Currency::add);
        Currency act = rwa.getYearlyTotal();
        assertEquals(exp, act);
    }

    private AccountId accountId(RowWithAccounts row) {
        return row.getAccountIds().get().findFirst().get();
    }
}
