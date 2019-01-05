package se.montesmites.ekonomi.report;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.model.tuple.AmountEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;
import se.montesmites.ekonomi.organization.Organization;

public class DataFetcher implements AccountsFetcher, AmountsFetcher {

  private static EntryCollector entryCollector(Organization organization) {
    return new EntryCollector(
        eventId ->
            organization.getEvent(eventId).map((Event event) -> toYearMonth(event.getDate())));
  }

  private static YearMonth toYearMonth(LocalDate date) {
    return YearMonth.of(date.getYear(), date.getMonth());
  }

  private final Organization organization;

  private final EntryAggregate entryAggregate;

  private final Map<java.time.Year, Set<Month>> touchedMonths;

  public DataFetcher(Organization organization) {
    this.organization = organization;
    this.entryAggregate = organization.streamEntries().collect(entryCollector(organization));
    this.touchedMonths = touchedMonths();
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
    return new AbstractMap.SimpleEntry<>(year(date), month(date));
  }

  private java.time.Year year(LocalDate date) {
    return java.time.Year.of(date.getYear());
  }

  private Month month(LocalDate date) {
    return date.getMonth();
  }

  @Override
  public Set<Month> touchedMonths(java.time.Year year) {
    return touchedMonths.getOrDefault(year, emptySet());
  }

  @Override
  public Stream<AccountId> streamAccountIds(java.time.Year year, Predicate<AccountId> filter) {
    return entryAggregate
        .getAggregate()
        .entrySet()
        .stream()
        .filter(e -> e.getKey().getYearMonth().getYear() == year.getValue())
        .map(e -> e.getKey().getAccountId())
        .filter(filter)
        .distinct()
        .sorted(comparing(AccountId::getId));
  }

  @Override
  public Optional<Balance> fetchBalance(AccountId accountId) {
    return organization.getBalance(accountId);
  }

  @Override
  public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
    return getAccountIdAmountMap(yearMonth).map(m -> m.get(accountId));
  }

  Optional<List<AccountIdAmountTuple>> getAccountIdAmountTuples(YearMonth yearMonth) {
    return Optional.of(
        streamEntryAggregateByYearMonth(yearMonth)
            .map(e -> new AccountIdAmountTuple(e.getKey().getAccountId(), e.getValue().getAmount()))
            .collect(toList()));
  }

  Optional<Map<AccountId, Currency>> getAccountIdAmountMap(YearMonth yearMonth) {
    return Optional.of(
        streamEntryAggregateByYearMonth(yearMonth)
            .collect(toMap(e -> e.getKey().getAccountId(), e -> e.getValue().getAmount())));
  }

  private Stream<Map.Entry<YearMonthAccountIdTuple, AmountEntryListTuple>>
      streamEntryAggregateByYearMonth(YearMonth yearMonth) {
    return entryAggregate
        .getAggregate()
        .entrySet()
        .stream()
        .filter(e -> e.getKey().getYearMonth().equals(yearMonth));
  }

  @Override
  public Optional<Account> getAccount(AccountId accountId) {
    return organization.getAccount(accountId);
  }
}
