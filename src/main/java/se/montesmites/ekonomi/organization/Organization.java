package se.montesmites.ekonomi.organization;

import se.montesmites.ekonomi.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class Organization {
    private final Collection<Account> accounts;
    private final Collection<Balance> balances;
    private final Collection<Entry> entries;
    private final Collection<Event> events;
    private final Collection<Year> years;

    private final Map<AccountId, Account> accountsByAccountId;
    private final Map<AccountId, Balance> balancesByAccountId;
    private final Map<EventId, List<Entry>> entriesByEventId;
    private final Map<EventId, Event> eventsByEventId;
    private final Map<java.time.Year, Year> yearsByYear;
    private final Map<YearId, Year> yearsByYearId;

    Organization(
            Stream<Account> accounts,
            Stream<Balance> balances,
            Stream<Entry> entries,
            Stream<Event> events,
            Stream<Year> years) {
        this.accounts = accounts.collect(toList());
        this.balances = balances.collect(toList());
        this.entries = entries.collect(toList());
        this.events = events.collect(toList());
        this.years = years.collect(toList());

        this.accountsByAccountId = this.accounts.stream()
                .collect(toMap(Account::getAccountId, identity()));
        this.balancesByAccountId = this.balances.stream()
                .collect(toMap(Balance::getAccountId, identity()));
        this.entriesByEventId = this.entries.stream()
                .collect(groupingBy(Entry::getEventId));
        this.eventsByEventId = this.events.stream()
                .collect(toMap(Event::getEventId, identity()));
        this.yearsByYear = this.years.stream()
                .collect(toMap(Year::getYear, identity()));
        this.yearsByYearId = this.years.stream()
                .collect(toMap(Year::getYearId, identity()));
    }
    
    public Stream<Account> streamAccounts() {
        return accounts.stream();
    }
    
    public Stream<Year> streamYears() {
        return years.stream();
    }
    
    public Stream<Entry> streamEntries() {
        return entries.stream();
    }
    
    public Optional<Year> getYear(java.time.Year year) {
        return Optional.ofNullable(yearsByYear.get(year));
    }
    
    public Optional<Year> getYear(YearId yearId) {
        return Optional.ofNullable(yearsByYearId.get(yearId));
    }

    public Optional<Event> getEvent(EventId eventId) {
        return Optional.ofNullable(eventsByEventId.get(eventId));
    }

    public Optional<List<Entry>> getEntries(EventId eventId) {
        return Optional.ofNullable(entriesByEventId.get(eventId));
    }
    
    public Optional<Account> getAccount(AccountId accountId) {
        return Optional.ofNullable(accountsByAccountId.get(accountId));
    }

    public Optional<Balance> getBalance(AccountId accountId) {
        return Optional.ofNullable(balancesByAccountId.get(accountId));
    }
}
