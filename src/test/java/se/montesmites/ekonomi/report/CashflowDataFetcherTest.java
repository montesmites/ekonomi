package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Map;
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
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountAggregate;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.test.util.EntryAggregateExpectedElements.BY_YEARMONTH_201201;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowDataFetcherTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final java.time.Year year = java.time.Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization);
    }

    @Test
    public void assertAccountIds() {
        final YearId yearId = new YearId("C");
        final List<AccountId> expAccountIds = Arrays.asList(1221, 1229, 1400,
                1510, 1650, 1710, 1910, 1920, 1930, 1940, 2091, 2098, 2099, 2440,
                2510, 2611, 2615, 2641, 2645, 2650, 2710, 2732, 2920, 2921, 2940,
                2941, 2995, 3041, 3045, 3048, 3051, 3055, 3058, 3540, 3590, 3592,
                3740, 3960, 4010, 4056, 4990, 5010, 5020, 5090, 5410, 5460, 5500,
                5611, 5612, 5613, 5615, 5800, 5930, 6071, 6072, 6090, 6110, 6211,
                6212, 6250, 6310, 6420, 6530, 6570, 6970, 7010, 7081, 7082, 7090,
                7210, 7281, 7285, 7290, 7385, 7399, 7411, 7510, 7519, 7533, 7570,
                7690, 7832, 8300, 8910, 8999).stream()
                .map(id -> Integer.toString(id))
                .map(id -> new AccountId(yearId, id))
                .collect(toList());
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

    @Test
    public void readAccountAmount_byYearMonth_2012January() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        Map<AccountId, Currency> actAmounts
                = new AccountIdAmountAggregate(
                        fetcher.getAccountIdAmountTuples(yearMonth).get())
                        .asAccountIdAmountMap();
        Map<AccountId, Currency> expAmounts
                = BY_YEARMONTH_201201.getAggregate(yearId).asAccountIdAmountMap();
        mapEqualityAssertion(expAmounts, actAmounts);
    }

    private <K, V> void mapEqualityAssertion(Map<K, V> expected, Map<K, V> actual) {
        assertEquals(expected.size(), expected.size());
        expected.entrySet().forEach(exp -> {
            K key = exp.getKey();
            V value = exp.getValue();
            assertTrue(key.toString(), actual.containsKey(key));
            assertEquals(key.toString(), value, actual.get(key));
        });
    }

    private void assertAccountYearMonthAmount(AccountId accountId, YearMonth yearMonth) {
        final Optional<Currency> exp
                = fetcher.getAccountIdAmountMap(yearMonth)
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
