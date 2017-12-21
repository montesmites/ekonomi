package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

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
        final CashflowDataFetcher fetcher = new CashflowDataFetcher(this.organization);
        final YearId yearId = organization.getYear(year).get().getYearId();
        this.accountId = new AccountId(yearId, "1920");
        final Supplier<Stream<AccountId>> accountIds = () -> Stream.of(accountId);
        final DefaultRowWithAccounts source = new DefaultRowWithAccounts(fetcher, accountIds, year, "");
        this.negated = new DefaultRowWithAccountsWithNegatedAmounts(source);
    }

    @Test
    void negateMonthlyAmount() {
        Column.streamMonths().forEach(month -> assertEquals(month.name(), entrySum(accountId, month), negated.getMonthlyAmount(month))
        );
    }

    @Test
    void negatedTexts() {
        Column.streamMonths().forEach(month -> assertEquals(month.name(), entrySum(accountId, month).format(), negated.formatText(month).trim())
        );
    }

    private Currency entrySum(AccountId accountId, Column month) {
        YearMonth yearMonth = YearMonth.of(2012, month.getMonth().get());
        return organization.streamEntries()
                .filter(entry -> entry.getAccountId().equals(accountId))
                .filter(entry -> entryYearMonth(entry).equals(yearMonth))
                .map(Entry::getAmount)
                .reduce(new Currency(0), Currency::add);
    }

    private YearMonth entryYearMonth(Entry entry) {
        LocalDate date = organization.getEvent(entry.getEventId()).get().getDate();
        return YearMonth.of(date.getYear(), date.getMonth());
    }
}
