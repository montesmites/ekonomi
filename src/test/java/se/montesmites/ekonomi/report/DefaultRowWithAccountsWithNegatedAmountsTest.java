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
import java.time.YearMonth;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DefaultTestDataExtension.class)
class DefaultRowWithAccountsWithNegatedAmountsTest {
    private final Year year = Year.of(2012);

    @OrganizationInjector
    private Organization organization;
    private CashflowReport report;
    private AccountId accountId;
    private DefaultRowWithAccountsWithNegatedAmounts negated;

    @BeforeEach
    void before() {
        var fetcher = new CashflowDataFetcher(this.organization);
        var yearId = organization.getYear(year).orElseThrow().getYearId();
        this.accountId = new AccountId(yearId, "1920");
        var accountIds = (Supplier<Stream<AccountId>>) () -> Stream.of(accountId);
        var source = new DefaultRowWithAccounts(fetcher, accountIds, year, "");
        this.negated = new DefaultRowWithAccountsWithNegatedAmounts(source);
    }

    @Test
    void negateMonthlyAmount() {
        Column.streamMonths()
              .forEach(
                      month ->
                              assertEquals(
                                      entrySum(accountId, month), negated.getMonthlyAmount(month), month.name()));
    }

    @Test
    void negatedTexts() {
        Column.streamMonths()
              .forEach(
                      month ->
                              assertEquals(
                                      entrySum(accountId, month).format(),
                                      negated.format(month).trim(),
                                      month.name()));
    }

    private Currency entrySum(AccountId accountId, Column month) {
        var yearMonth = YearMonth.of(2012, month.getMonth().orElseThrow());
        return organization
                .streamEntries()
                .filter(entry -> entry.getAccountId().equals(accountId))
                .filter(entry -> entryYearMonth(entry).equals(yearMonth))
                .map(Entry::getAmount)
                .reduce(new Currency(0), Currency::add);
    }

    private YearMonth entryYearMonth(Entry entry) {
        var date = organization.getEvent(entry.getEventId()).orElseThrow().getDate();
        return YearMonth.of(date.getYear(), date.getMonth());
    }
}
