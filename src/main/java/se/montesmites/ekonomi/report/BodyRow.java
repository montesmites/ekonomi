package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Set;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class BodyRow implements Row {

    private final CashflowDataFetcher fetcher;
    private final Set<AccountId> accountIds;
    private final java.time.Year year;
    private final String description;

    public BodyRow(CashflowDataFetcher fetcher, Set<AccountId> accountIds, java.time.Year year, String description) {
        this.fetcher = fetcher;
        this.accountIds = accountIds;
        this.year = year;
        this.description = description;
    }

    public Set<AccountId> getAccountIds() {
        return accountIds;
    }

    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return description;
            case TOTAL:
                return getYearlyTotal().format();
            default:
                return getMonthlyAmount(column).format();
        }
    }

    public Currency getMonthlyAmount(Column column) {
        return accountIds.stream()
                .map(acc -> getMonthlyAmount(acc, column.getMonth().get()))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    private Currency getMonthlyAmount(AccountId accountId, Month month) {
        return getMonthlyAmount(accountId, YearMonth.of(year.getValue(), month));
    }

    private Currency getMonthlyAmount(AccountId accountId, YearMonth yearMonth) {
        return fetcher.fetchAmount(accountId, yearMonth).orElse(new Currency(0));
    }

    public Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyAmount)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
}
