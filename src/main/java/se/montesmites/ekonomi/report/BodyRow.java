package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.Optional;
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

    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return accountId.getId();
            case TOTAL:
                return getYearlyTotal().format();
            default:
                return getMonthlyAmount(column).orElse(new Currency(0)).format();
        }
    }

    public Optional<Currency> getMonthlyAmount(Column column) {
        return column.getMonth()
                .flatMap(month
                        -> fetcher.fetchAmount(
                        accountId,
                        YearMonth.of(
                                year.getValue(),
                                month)));
    }

    public Currency getYearlyTotal() {
        return Column.stream()
                .map(this::getMonthlyAmount)
                .map(o -> o.orElse(new Currency(0)))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
}
