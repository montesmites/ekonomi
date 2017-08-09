package se.montesmites.ekonomi.report;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountAggregate;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.organization.Organization;

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
    private final Map<YearMonth, Map<AccountId, Currency>> accountAmountByYearMonthMap;

    public CashflowDataFetcher(Organization organization) {
        this.organization = organization;
        this.entryAggregate
                = organization.streamEntries()
                        .collect(entryCollector(organization));

        Map<YearMonth, AccountIdAmountAggregate> aggregatesMap
                = accountIdAmountMap(
                        organization.streamEntries(),
                        entry -> YearMonth.from(entryDate(entry)));
        this.accountAmountByYearMonthMap
                = aggregatesMap.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey,
                                e -> e.getValue().asAccountIdAmountMap()));
    }

    public Stream<AccountId> streamAccountIds(Year year) {
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
                = getAccountIdAmountMap(yearMonth).map(m -> m.get(accountId));
        return amount;
    }

    List<AccountIdAmountTuple> getAccountIdAmountTuples(YearMonth yearMonth) {
        return entryAggregate.getAggregate().entrySet().stream()
                .filter(e -> e.getKey().getYearMonth().equals(yearMonth))
                .map(e
                        -> new AccountIdAmountTuple(
                        e.getKey().getAccountId(),
                        e.getValue().getAmount()))
                .collect(toList());
    }

    Optional<Map<AccountId, Currency>> getAccountIdAmountMap(YearMonth yearMonth) {
        return Optional.ofNullable(accountAmountByYearMonthMap.get(yearMonth));
    }

    private <T> Map<T, AccountIdAmountAggregate> accountIdAmountMap(Stream<Entry> entries, Function<Entry, T> keyMapper) {
        return entries
                .collect(
                        toMap(
                                keyMapper::apply,
                                AccountIdAmountAggregate::new,
                                AccountIdAmountAggregate::merge
                        ));
    }

    private LocalDate entryDate(Entry entry) {
        return organization.getEvent(entry.getEventId()).get().getDate();
    }
}
