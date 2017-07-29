package se.montesmites.ekonomi.organization;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;
import static java.util.Comparator.*;
import java.util.Map;
import static java.util.stream.Collectors.*;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.EntryEvent;
import se.montesmites.ekonomi.model.EntryStatus;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountAggregate;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;

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
    public void readAccountAmount_byDate_20120112() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        List<AccountIdAmountTuple> expTuples = Arrays.asList(
                tuple(yearId, 1650, -1085600),
                tuple(yearId, 1920, -50000000),
                tuple(yearId, 1930, -8365353),
                tuple(yearId, 1940, 50000000),
                tuple(yearId, 2440, 1463852),
                tuple(yearId, 2510, 1400000),
                tuple(yearId, 2710, 3361000),
                tuple(yearId, 2940, 3527105),
                tuple(yearId, 3740, -005),
                tuple(yearId, 3960, 001),
                tuple(yearId, 6570, 8000),
                tuple(yearId, 7510, -309000))
                .stream().collect(toList());
        LocalDate date = LocalDate.parse("2012-01-12");
        Map<AccountId, Currency> actAmounts = organization.getEntries(date).get();
        Map<AccountId, Currency> expAmounts
                = new AccountIdAmountAggregate(expTuples).asAccountIdAmountMap();
        mapEqualityAssertion(expAmounts, actAmounts);
    }
    
    @Test
    public void readAccountAmount_byYearMonth_2012January() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        List<AccountIdAmountTuple> expTuples = Arrays.asList(
                tuple(yearId, 1400, 12077000),
                tuple(yearId, 1510, -32556400),
                tuple(yearId, 1650, -98200),
                tuple(yearId, 1710, 3200000),
                tuple(yearId, 1910, -117000),
                tuple(yearId, 1920, -29543100),
                tuple(yearId, 1930, -7217100),
                tuple(yearId, 1940, 50000000),
                tuple(yearId, 2440, 881799),
                tuple(yearId, 2510, 1400000),
                tuple(yearId, 2710, 49700),
                tuple(yearId, 2920, -711413),
                tuple(yearId, 2940, -2588),
                tuple(yearId, 2941, -229645),
                tuple(yearId, 3041, -12807500),
                tuple(yearId, 3051, -4370000),
                tuple(yearId, 3590, -147000),
                tuple(yearId, 3740, 98),
                tuple(yearId, 3960, -764999),
                tuple(yearId, 4010, 15099000),
                tuple(yearId, 4990, -12077000),
                tuple(yearId, 5010, 1600000),
                tuple(yearId, 5090, 100320),
                tuple(yearId, 5410, 446650),
                tuple(yearId, 5611, 353544),
                tuple(yearId, 5615, 371875),
                tuple(yearId, 6071, 40000),
                tuple(yearId, 6212, 256800),
                tuple(yearId, 6250, 44000),
                tuple(yearId, 6570, 8000),
                tuple(yearId, 7010, 5188204),
                tuple(yearId, 7082, 553604),
                tuple(yearId, 7090, 135413),
                tuple(yearId, 7210, 4800000),
                tuple(yearId, 7290, 576000),
                tuple(yearId, 7385, 413700),
                tuple(yearId, 7399, -413700),
                tuple(yearId, 7510, 3220693),
                tuple(yearId, 7519, 229645),
                tuple(yearId, 7690, 9600)
        );
        YearMonth yearMonth = YearMonth.of(2012, Month.JANUARY);
        Map<AccountId, Currency> actAmounts = organization.getEntries(yearMonth).get();
        Map<AccountId, Currency> expAmounts
                = new AccountIdAmountAggregate(expTuples).asAccountIdAmountMap();
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

    private AccountIdAmountTuple tuple(YearId yearId, int account, long amount) {
        return new AccountIdAmountTuple(
                new AccountId(yearId, Integer.toString(account)),
                new Currency(amount));
    }
}
