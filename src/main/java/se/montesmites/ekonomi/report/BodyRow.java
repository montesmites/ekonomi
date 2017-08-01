package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import java.util.Optional;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class BodyRow implements Row {

    private final CashflowDataFetcher fetcher;
    private final AccountId accountId;
    private final java.time.Year year;

    public BodyRow(CashflowDataFetcher fetcher, AccountId accountId, java.time.Year year) {
        this.fetcher = fetcher;
        this.accountId = accountId;
        this.year = year;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public Optional<Currency> getMonthlyAmount(YearMonth yearMonth) {
        return fetcher.fetchAmount(accountId, yearMonth);
    }

    public Currency getYearlyTotal() {
        return yearMonths()
                .map(this::getMonthlyAmount)
                .map(o -> o.orElse(new Currency(0)))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    private Stream<YearMonth> yearMonths() {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }
}
