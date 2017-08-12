package se.montesmites.ekonomi.organization;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.*;

public class Organization {

    public static Organization fromPath(Path path) {
        Parser p = new Parser(path);
        return new Organization(
                p.parse(ACCOUNTS),
                p.parse(BALANCES),
                p.parse(ENTRIES),
                p.parse(EVENTS),
                p.parse(YEARS));
    }
    
    private final Collection<Account> accounts;
    private final Collection<Year> years;
    private final Collection<Entry> entries;
    
    private final Map<AccountId, Account> accountsByAccountId;
    private final Map<AccountId, Balance> balancesByAccountId;
    private final Map<EventId, List<Entry>> entriesByEventId;
    private final Map<EventId, Event> eventsByEventId;
    private final Map<java.time.Year, Year> yearsByYear;
    private final Map<YearId, Year> yearsByYearId;

    private Organization(
            Collection<Account> accounts,
            Collection<Balance> balances,
            Collection<Entry> entries,
            Collection<Event> events,
            Collection<Year> years) {
        this.accounts = accounts;
        this.years = years;
        this.entries = entries;
        
        this.accountsByAccountId = accounts.stream()
                .collect(toMap(Account::getAccountId, identity()));
        this.balancesByAccountId = balances.stream()
                .collect(toMap(Balance::getAccountId, identity()));
        this.entriesByEventId = entries.stream()
                .collect(groupingBy(Entry::getEventId));
        this.eventsByEventId = events.stream()
                .collect(toMap(Event::getEventId, identity()));
        this.yearsByYear = years.stream()
                .collect(toMap(Year::getYear, identity()));
        this.yearsByYearId = years.stream()
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
