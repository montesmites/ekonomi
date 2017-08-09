package se.montesmites.ekonomi.report;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.model.tuple.CurrencyEntryListTuple;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;
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

    public CashflowDataFetcher(Organization organization) {
        this.organization = organization;
        this.entryAggregate
                = organization.streamEntries()
                        .collect(entryCollector(organization));
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

    private Stream<Map.Entry<YearMonthAccountIdTuple, CurrencyEntryListTuple>> streamEntryAggregateByYearMonth(YearMonth yearMonth) {
        return entryAggregate.getAggregate().entrySet().stream()
                .filter(e -> e.getKey().getYearMonth().equals(yearMonth));
    }
}
