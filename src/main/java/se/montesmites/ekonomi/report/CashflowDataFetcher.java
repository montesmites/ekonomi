package se.montesmites.ekonomi.report;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountAggregate;
import se.montesmites.ekonomi.model.tuple.AccountIdAmountTuple;
import se.montesmites.ekonomi.organization.Organization;

public class CashflowDataFetcher {

    private final Organization organization;
    private final Map<YearMonth, List<AccountIdAmountTuple>> accountAmountByYearMonth;
    private final Map<YearMonth, Map<AccountId, Currency>> accountAmountByYearMonthMap;

    public CashflowDataFetcher(Organization organization) {
        this.organization = organization;

        Map<YearMonth, AccountIdAmountAggregate> aggregatesMap
                = accountIdAmountMap(
                        organization.streamEntries(),
                        entry -> YearMonth.from(entryDate(entry)));
        this.accountAmountByYearMonth
                = aggregatesGrouper(aggregatesMap);
        this.accountAmountByYearMonthMap
                = aggregatesMap.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey,
                                e -> e.getValue().asAccountIdAmountMap()));
    }

    public Stream<AccountId> streamAccountIds(Year year) {
        return yearMonths(year).flatMap(
                ym -> getAccountIdAmountTuples(ym).get().stream()
                        .flatMap(t -> Stream.of(t.getAccountId())))
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

    private Stream<YearMonth> yearMonths(Year year) {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }

    Optional<List<AccountIdAmountTuple>> getAccountIdAmountTuples(YearMonth yearMonth) {
        return Optional.ofNullable(accountAmountByYearMonth.get(yearMonth));
    }

    Optional<Map<AccountId, Currency>> getAccountIdAmountMap(YearMonth yearMonth) {
        return Optional.ofNullable(accountAmountByYearMonthMap.get(yearMonth));
    }

    private <T> Map<T, List<AccountIdAmountTuple>> aggregatesGrouper(
            Map<T, AccountIdAmountAggregate> aggregates) {
        return aggregates.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getTuples()));
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
