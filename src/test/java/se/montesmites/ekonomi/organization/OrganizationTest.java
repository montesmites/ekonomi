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
        organization.streamYears().map(Year::year).collect(toList());
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
            .collect(groupingBy(e -> e.eventId().yearId().id(), counting()));
    assertEquals(expCount.entrySet(), actCount.entrySet());
  }

  @Test
  void readYear_byYear_2012() {
    Year year = organization.getYear(java.time.Year.of(2012)).get();
    YearId yearId = new YearId("C");
    assertEquals(yearId, year.yearId());
    assertEquals(LocalDate.parse("2012-01-01"), year.from());
    assertEquals(LocalDate.parse("2012-12-31"), year.to());
  }

  @Test
  void readEvent_byEventId_2012A1() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().yearId();
    EventId eventId = new EventId(yearId, 1, new Series("A"));
    Event event = organization.getEvent(eventId).get();
    assertEquals(eventId, event.eventId());
    assertEquals(LocalDate.parse("2012-01-12"), event.date());
    assertEquals("Överföring till sparkonto", event.description());
    assertEquals(LocalDate.parse("2011-01-31"), event.registrationDate());
  }

  @Test
  void readEntries_byEventId_2012A1() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().yearId();
    EventId eventId = new EventId(yearId, 1, new Series("A"));
    List<Entry> actEntries =
        organization
            .getEntries(eventId)
            .get()
            .stream()
            .sorted(comparing(entry -> entry.accountId().id()))
            .collect(toList());
    List<Entry> expEntries =
        List.of(entry(eventId, 1920, -50000000), entry(eventId, 1940, 50000000));
    assertEquals(expEntries, actEntries);
  }

  @Test
  void readAccount_byAccountId_20121920() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().yearId();
    AccountId accountId = new AccountId(yearId, "1920");
    Account account = organization.getAccount(accountId).get();
    assertEquals(accountId, account.accountId());
    assertEquals(AccountStatus.OPEN, account.accountStatus());
    assertEquals("Bank, PlusGiro", account.description());
  }

  @Test
  void readBalance_byAccountId_20121920() {
    YearId yearId = organization.getYear(java.time.Year.of(2012)).get().yearId();
    AccountId accountId = new AccountId(yearId, "1920");
    Balance balance = organization.getBalance(accountId).get();
    assertEquals(accountId, balance.accountId());
    assertEquals(currency(83340012), balance.balance());
  }

  private Entry entry(EventId eventId, int account, long amount) {
    return new Entry(
        eventId,
        new AccountId(eventId.yearId(), Integer.toString(account)),
        currency(amount),
        new EntryStatus(EntryStatus.Status.ACTIVE, EntryEvent.ORIGINAL));
  }

  private Currency currency(long amount) {
    return new Currency(amount);
  }
}
