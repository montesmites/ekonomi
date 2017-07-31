package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;
import static java.util.Comparator.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.Assert.*;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;

public class AccountAmountFetcherTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private AccountAmountFetcher fetcher;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new AccountAmountFetcher(this.organization);
    }

    @Test
    public void assertAccountIds() {
        final List<AccountId> expAccountIds = yearMonths().flatMap(
                ym -> organization.getAccountIdAmountTuples(ym).get().stream().map(
                        t -> t.getAccountId())).distinct().sorted(
                        comparing(AccountId::getId)).collect(toList());
        final List<AccountId> actAccountIds
                = fetcher.streamAccountIds(year).collect(toList());
        assertEquals(expAccountIds, actAccountIds);
    }

    @Test
    public void assertYearMonthColumns() {
        fetcher.streamAccountIds(year)
                .forEach(accountId -> yearMonths()
                .forEach(ym -> assertAccountYearMonthAmount(accountId, ym)));
    }

    @Test
    public void assertBalances() {
        fetcher.streamAccountIds(year)
                .forEach(accountId -> assertBalance(accountId));
    }

    private void assertAccountYearMonthAmount(AccountId accountId, YearMonth yearMonth) {
        final Optional<Currency> exp
                = organization.getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(accountId));
        final Optional<Currency> act = fetcher.fetchAmount(accountId, yearMonth);
        String fmt = "%s (%s)";
        String msg = String.format(fmt, accountId.getId(), yearMonth);
        assertEquals(msg, exp, act);
    }

    private void assertBalance(AccountId accountId) {
        final Optional<Balance> exp = organization.getBalance(accountId);
        final Optional<Balance> act = fetcher.fetchBalance(accountId);
        String fmt = "%s";
        String msg = String.format(fmt, accountId.getId());
        assertEquals(msg, exp, act);
    }

    private Stream<YearMonth> yearMonths() {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }
}
