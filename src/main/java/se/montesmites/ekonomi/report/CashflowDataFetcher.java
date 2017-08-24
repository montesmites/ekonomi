package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.*;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.model.tuple.AmountEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;
import se.montesmites.ekonomi.organization.Organization;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class CashflowDataFetcher {

    private static EntryCollector entryCollector(Organization organization) {
        return new EntryCollector(
                eventId -> organization.getEvent(eventId)
                        .map((Event event) -> toYearMonth(event.getDate())));
    }

    private static YearMonth toYearMonth(LocalDate date) {
        return YearMonth.of(date.getYear(), date.getMonth());
    }

    private final Organization organization;

    private final EntryAggregate entryAggregate;

    private final Map<java.time.Year, Set<Month>> touchedMonths;

    public CashflowDataFetcher(Organization organization) {
        this.organization = organization;
        this.entryAggregate
                = organization.streamEntries()
                        .collect(entryCollector(organization));
        this.touchedMonths = touchedMonths();
    }

    public Set<Month> getTouchedMonths(java.time.Year year) {
        return touchedMonths.getOrDefault(year, emptySet());
    }

    private Map<java.time.Year, Set<Month>> touchedMonths() {
        return organization
                .streamEntries()
                .map(Entry::getEventId)
                .map(organization::getEvent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Event::getDate)
                .map(this::entry)
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toSet())));
    }

    private Map.Entry<java.time.Year, Month> entry(LocalDate date) {
        return new AbstractMap.SimpleEntry<java.time.Year, Month>(year(date), month(date));
    }

    private java.time.Year year(LocalDate date) {
        return java.time.Year.of(date.getYear());
    }

    private Month month(LocalDate date) {
        return date.getMonth();
    }

    public Set<Month> touchedMonths(java.time.Year year) {
        return touchedMonths.getOrDefault(year, emptySet());
    }

    public Stream<AccountId> streamAccountIds(java.time.Year year) {
        return entryAggregate.getAggregate().entrySet().stream()
                .filter(e -> e.getKey().getYearMonth().getYear() == year.getValue())
                .map(e -> e.getKey().getAccountId())
                .distinct()
                .sorted(comparing(AccountId::getId));
    }

    public Optional<Balance> fetchBalance(AccountId accountId) {
        return organization.getBalance(accountId);
    }

    public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        Optional<Currency> amount
                = getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(accountId));
        return amount;
    }

    public EntryAggregate getEntryAggregate() {
        return entryAggregate;
    }

    Optional<List<AccountIdAmountTuple>> getAccountIdAmountTuples(YearMonth yearMonth) {
        return Optional.of(
                streamEntryAggregateByYearMonth(yearMonth)
                        .map(e
                                -> new AccountIdAmountTuple(
                                e.getKey().getAccountId(),
                                e.getValue().getAmount()))
                        .collect(toList())
        );
    }

    Optional<Map<AccountId, Currency>> getAccountIdAmountMap(YearMonth yearMonth) {
        return Optional.of(
                streamEntryAggregateByYearMonth(yearMonth)
                        .collect(
                                toMap(
                                        e -> e.getKey().getAccountId(),
                                        e -> e.getValue().getAmount()))
        );
    }

    private Stream<Map.Entry<YearMonthAccountIdTuple, AmountEntryListTuple>> streamEntryAggregateByYearMonth(YearMonth yearMonth) {
        return entryAggregate.getAggregate().entrySet().stream()
                .filter(e -> e.getKey().getYearMonth().equals(yearMonth));
    }
}
