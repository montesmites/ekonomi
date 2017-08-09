package se.montesmites.ekonomi.organization;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import static java.util.Comparator.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.EntryEvent;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountAggregate;
import static se.montesmites.ekonomi.test.util.AccountIdAmountAggregateExpectedElements.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class OrganizationTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private Organization organization;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
    }

    @Test
    public void streamYears() throws Exception {
        final List<java.time.Year> expYears
                = Arrays.asList(2012, 2013, 2014, 2015).stream()
                        .map(java.time.Year::of)
                        .collect(toList());
        final List<java.time.Year> actYears
                = organization.streamYears()
                        .map(y -> y.getYear())
                        .collect(toList());
        assertEquals(expYears, actYears);
    }

    @Test
    public void streamEntries() throws Exception {
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 1306);
                put("D", (long) 1404);
                put("E", (long) 1344);
                put("F", (long) 218);
            }
        };
        final Map<String, Long> actCount = organization.streamEntries()
                .collect(groupingBy(
                        e -> e.getEventId().getYearId().getId(),
                        counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    public void readYear_byYear_2012() throws Exception {
        Year year = organization.getYear(java.time.Year.of(2012)).get();
        YearId yearId = new YearId("C");
        assertEquals(yearId, year.getYearId());
        assertEquals(LocalDate.parse("2012-01-01"), year.getFrom());
        assertEquals(LocalDate.parse("2012-12-31"), year.getTo());
    }

    @Test
    public void readEvent_byEventId_2012A1() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        EventId eventId = new EventId(yearId, 1, new Series("A"));
        Event event = organization.getEvent(eventId).get();
        assertEquals(eventId, event.getEventId());
        assertEquals(LocalDate.parse("2012-01-12"), event.getDate());
        assertEquals("Överföring till sparkonto", event.getDescription());
        assertEquals(LocalDate.parse("2011-01-31"), event.getRegistrationDate());
    }

    @Test
    public void readEntries_byEventId_2012A1() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        EventId eventId = new EventId(yearId, 1, new Series("A"));
        List<Entry> actEntries = organization.getEntries(eventId).get().stream()
                .sorted(comparing(entry -> entry.getAccountId().getId()))
                .collect(toList());
        List<Entry> expEntries = Arrays.asList(
                entry(eventId, 1920, -50000000),
                entry(eventId, 1940, 50000000));
        assertEquals(expEntries, actEntries);
    }

    @Test
    public void readAccountAmount_byYearMonth_2012January() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        Map<AccountId, Currency> actAmounts = new AccountIdAmountAggregate(
                organization.getAccountIdAmountTuples(yearMonth).get()).asAccountIdAmountMap();
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

    @Test
    public void readAccount_byAccountId_20121920() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        AccountId accountId = new AccountId(yearId, "1920");
        Account account = organization.getAccount(accountId).get();
        assertEquals(accountId, account.getAccountId());
        assertEquals(AccountStatus.OPEN, account.getAccountStatus());
        assertEquals("Bank, PlusGiro", account.getDescription());
    }

    @Test
    public void readBalance_byAccountId_20121920() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        AccountId accountId = new AccountId(yearId, "1920");
        Balance balance = organization.getBalance(accountId).get();
        assertEquals(accountId, balance.getAccountId());
        assertEquals(currency(83340012), balance.getBalance());
    }

    private Entry entry(EventId eventId, int account, long amount) {
        return new Entry(
                eventId,
                new AccountId(
                        eventId.getYearId(),
                        Integer.toString(account)),
                currency(amount),
                new EntryStatus(EntryStatus.Status.ACTIVE, EntryEvent.ORIGINAL));
    }

    private Currency currency(long amount) {
        return new Currency(amount);
    }
}
