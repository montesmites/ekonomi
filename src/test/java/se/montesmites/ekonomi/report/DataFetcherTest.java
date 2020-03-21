package se.montesmites.ekonomi.report;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.test.util.EntryAggregateExpectedElements.BY_YEARMONTH_201201;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class DataFetcherTest {

  private final java.time.Year year = java.time.Year.of(2012);

  @OrganizationInjector private Organization organization;
  private DataFetcher fetcher;

  @BeforeEach
  void before() {
    this.fetcher = new DataFetcher(this.organization);
  }

  @Test
  void assertAccountIds() {
    var yearId = new YearId("C");
    var expAccountIds =
        Stream.of(
                1221, 1229, 1400, 1510, 1650, 1710, 1910, 1920, 1930, 1940, 2091, 2098, 2099, 2440,
                2510, 2611, 2615, 2641, 2645, 2650, 2710, 2732, 2920, 2921, 2940, 2941, 2995, 3041,
                3045, 3048, 3051, 3055, 3058, 3540, 3590, 3592, 3740, 3960, 4010, 4056, 4990, 5010,
                5020, 5090, 5410, 5460, 5500, 5611, 5612, 5613, 5615, 5800, 5930, 6071, 6072, 6090,
                6110, 6211, 6212, 6250, 6310, 6420, 6530, 6570, 6970, 7010, 7081, 7082, 7090, 7210,
                7281, 7285, 7290, 7385, 7399, 7411, 7510, 7519, 7533, 7570, 7690, 7832, 8300, 8910,
                8999)
            .map(id -> Integer.toString(id))
            .map(id -> new AccountId(yearId, id))
            .collect(toList());
    var actAccountIds = fetcher.streamAccountIds(year, __ -> true).collect(toList());
    assertEquals(expAccountIds, actAccountIds);
  }

  @Test
  void assertYearMonthColumns() {
    fetcher
        .streamAccountIds(year, __ -> true)
        .forEach(
            accountId -> yearMonths().forEach(ym -> assertAccountYearMonthAmount(accountId, ym)));
  }

  @Test
  void assertBalances() {
    fetcher.streamAccountIds(year, __ -> true).forEach(this::assertBalance);
  }

  @Test
  void readAccountAmount_byYearMonth_2012January() {
    var yearId = organization.getYear(java.time.Year.of(2012)).orElseThrow().yearId();
    var yearMonth = YearMonth.of(2012, Month.JANUARY);
    var actAmounts =
        fetcher
            .getAccountIdAmountTuples(yearMonth)
            .orElseThrow()
            .stream()
            .collect(toMap(AccountIdAmountTuple::accountId, AccountIdAmountTuple::amount));
    var expAmounts = BY_YEARMONTH_201201.getAggregate(yearId);
    mapEqualityAssertion(expAmounts, actAmounts);
  }

  private <K, V> void mapEqualityAssertion(Map<K, V> expected, Map<K, V> actual) {
    assertEquals(expected.size(), expected.size());
    expected.forEach(
        (key, value) -> {
          assertTrue(actual.containsKey(key), key.toString());
          assertEquals(value, actual.get(key), key.toString());
        });
  }

  private void assertAccountYearMonthAmount(AccountId accountId, YearMonth yearMonth) {
    var exp =
        fetcher.getAccountIdAmountMap(yearMonth).map(m -> m.get(accountId));
    var act = fetcher.fetchAmount(accountId, yearMonth);
    var fmt = "%s (%s)";
    var msg = String.format(fmt, accountId.id(), yearMonth);
    assertEquals(exp, act, msg);
  }

  private void assertBalance(AccountId accountId) {
    var exp = organization.getBalance(accountId);
    var act = fetcher.fetchBalance(accountId);
    var fmt = "%s";
    var msg = String.format(fmt, accountId.id());
    assertEquals(exp, act, msg);
  }

  private Stream<YearMonth> yearMonths() {
    return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
  }
}
