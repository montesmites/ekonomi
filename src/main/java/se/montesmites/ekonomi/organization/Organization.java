package se.montesmites.ekonomi.organization;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Year;

public class Organization {

  private final EventManager eventManager;

  private final Collection<Entry> entries;
  private final Collection<Year> years;

  private final Map<AccountId, Account> accountsByAccountId;
  private final Map<AccountId, Balance> balancesByAccountId;
  private final Map<EventId, List<Entry>> entriesByEventId;
  private final Map<java.time.Year, Year> yearsByYear;

  public Organization(
      EventManager eventManager,
      Stream<Account> accounts,
      Stream<Balance> balances,
      Stream<Entry> entries,
      Stream<Year> years) {
    this.eventManager = eventManager;
    this.entries = entries.collect(toList());
    this.years = years.collect(toList());

    this.accountsByAccountId = accounts.collect(toMap(Account::accountId, identity()));
    this.balancesByAccountId =
        balances.collect(toList()).stream().collect(toMap(Balance::accountId, identity()));
    this.entriesByEventId = this.entries.stream().collect(groupingBy(Entry::eventId));
    this.yearsByYear = this.years.stream().collect(toMap(Year::year, identity()));
  }

  public Stream<Year> streamYears() {
    return years.stream();
  }

  public Stream<Account> streamAccounts() {
    return accountsByAccountId.values().stream();
  }

  public Stream<Balance> streamBalances() {
    return balancesByAccountId.values().stream();
  }

  public Stream<Event> streamEvents() {
    return eventManager.stream();
  }

  public Stream<Entry> streamEntries() {
    return entries.stream();
  }

  public Optional<Year> getYear(java.time.Year year) {
    return Optional.ofNullable(yearsByYear.get(year));
  }

  public Optional<Event> getEvent(EventId eventId) {
    return eventManager.getEvent(eventId);
  }

  Optional<List<Entry>> getEntries(EventId eventId) {
    return Optional.ofNullable(entriesByEventId.get(eventId));
  }

  public Optional<Account> getAccount(AccountId accountId) {
    return Optional.ofNullable(accountsByAccountId.get(accountId));
  }

  public Optional<Balance> getBalance(AccountId accountId) {
    return Optional.ofNullable(balancesByAccountId.get(accountId));
  }
}
