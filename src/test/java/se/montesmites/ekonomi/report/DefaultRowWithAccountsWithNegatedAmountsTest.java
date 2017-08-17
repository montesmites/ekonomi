package se.montesmites.ekonomi.report;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class DefaultRowWithAccountsWithNegatedAmountsTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private YearId yearId;
    private AccountId accountId;
    private Supplier<Stream<AccountId>> accountIds;
    private DefaultRowWithAccounts source;
    private DefaultRowWithAccountsWithNegatedAmounts negated;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.yearId = organization.getYear(year).get().getYearId();
        this.accountId = new AccountId(yearId, "1920");
        this.accountIds = () -> Stream.of(accountId);
        this.source = new DefaultRowWithAccounts(fetcher, accountIds, year, "");
        this.negated = new DefaultRowWithAccountsWithNegatedAmounts(source);
    }

    @Test
    public void negateMonthlyAmount() {
        Column.streamMonths().forEach(month
                -> assertEquals(
                        month.name(),
                        entrySum(accountId, month),
                        negated.getMonthlyAmount(month))
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
