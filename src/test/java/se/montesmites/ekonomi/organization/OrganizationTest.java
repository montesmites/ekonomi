package se.montesmites.ekonomi.organization;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class OrganizationTest {

  @OrganizationInjector private Organization organization;

  @Test
  void streamYears() {
    final List<java.time.Year> expYears =
        Stream.of(2012, 2013, 2014, 2015).map(java.time.Year::of).collect(toList());
    final List<java.time.Year> actYears =
        organization.streamYears().map(Year::getYear).collect(toList());
    assertEquals(expYears, actYears);
  }

  @Test
  void streamEntries() {
    final Map<String, Long> expCount =
        new HashMap<>() {
          {
            put("C", (long) 1313);
            put("D", (long) 1404);
            put("E", (long) 1344);
            put("F", (long) 218);
          }
        };
    final Map<String, Long> actCount =
        organization
            .streamEntries()
            .collect(groupingBy(e -> e.getEventId().getYearId().getId(), counting()));
    assertEquals(expCount.entrySet(), actCount.entrySet());
  }

  @Test
  void readYear_byYear_2012() {
    Year year = organization.getYear(java.time.Year.of(2012)).get();
    YearId yearId = new YearId("C");
    assertEquals(yearId, year.getYearId());
    assertEquals(LocalDate.parse("2012-01-01"), year.getFrom());
    assertEquals(LocalDate.parse("2012-12-31"), year.getTo());
  }

  @Test
  void readEvent_byEventId_2012A1() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
    EventId eventId = new EventId(yearId, 1, new Series("A"));
    Event event = organization.getEvent(eventId).get();
    assertEquals(eventId, event.getEventId());
    assertEquals(LocalDate.parse("2012-01-12"), event.getDate());
    assertEquals("Överföring till sparkonto", event.getDescription());
    assertEquals(LocalDate.parse("2011-01-31"), event.getRegistrationDate());
  }

  @Test
  void readEntries_byEventId_2012A1() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
    EventId eventId = new EventId(yearId, 1, new Series("A"));
    List<Entry> actEntries =
        organization
            .getEntries(eventId)
            .get()
            .stream()
            .sorted(comparing(entry -> entry.getAccountId().getId()))
            .collect(toList());
    List<Entry> expEntries =
        List.of(entry(eventId, 1920, -50000000), entry(eventId, 1940, 50000000));
    assertEquals(expEntries, actEntries);
  }

  @Test
  void readAccount_byAccountId_20121920() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
    AccountId accountId = new AccountId(yearId, "1920");
    Account account = organization.getAccount(accountId).get();
    assertEquals(accountId, account.getAccountId());
    assertEquals(AccountStatus.OPEN, account.getAccountStatus());
    assertEquals("Bank, PlusGiro", account.getDescription());
  }

  @Test
  void readBalance_byAccountId_20121920() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
    AccountId accountId = new AccountId(yearId, "1920");
    Balance balance = organization.getBalance(accountId).get();
    assertEquals(accountId, balance.getAccountId());
    assertEquals(currency(83340012), balance.getBalance());
  }

  private Entry entry(EventId eventId, int account, long amount) {
    return new Entry(
        eventId,
        new AccountId(eventId.getYearId(), Integer.toString(account)),
        currency(amount),
        new EntryStatus(EntryStatus.Status.ACTIVE, EntryEvent.ORIGINAL));
  }

  private Currency currency(long amount) {
    return new Currency(amount);
  }
}
