package se.montesmites.ekonomi.report;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import static se.montesmites.ekonomi.report.Column.*;

public class AccumulatingRow implements RowWithAccounts {

    private final CashflowDataFetcher fetcher;
    private final Supplier<Stream<AccountId>> accountIds;
    private final DefaultRowWithAccounts monthlyNetAmounts;
    private final Map<Column, Currency> amounts;
    private final Coefficient coefficient;

    public AccumulatingRow(CashflowDataFetcher fetcher, Supplier<Stream<AccountId>> accountIds, java.time.Year year, Coefficient coefficient) {
        this.fetcher = fetcher;
        this.accountIds = accountIds;
        this.monthlyNetAmounts
                = new DefaultRowWithAccounts(fetcher, accountIds, year, "");
        this.coefficient = coefficient;
        this.amounts = getAmounts();
    }

    @Override
    public Supplier<Stream<AccountId>> getAccountIds() {
        return accountIds;
    }

    @Override
    public String getText(Column column) {
        return amounts.get(column).format();
    }

    @Override
    public Currency getMonthlyAmount(Column column) {
        return amounts.get(column);
    }

    @Override
    public Currency getYearlyTotal() {
        return amounts.get(DECEMBER);
    }
    
    public Currency getBalance() {
        return amounts.get(DESCRIPTION);
    }
    
    private Map<Column, Currency> getAmounts() {
        Map<Column, Currency> map = new EnumMap<>(Column.class);
        Currency accumulator = new Currency(0);
        for (Column column : Column.values()) {
            Currency net = coefficient.apply(columnNetAmount(column));
            Currency columnBalance = accumulator.add(net);
            map.put(column, columnBalance);
            accumulator = columnBalance;
        }
        return map;
    }

    private Currency columnNetAmount(Column column) {
        switch (column) {
            case DESCRIPTION: return balance();
            case TOTAL: return new Currency(0);
            default: return monthlyNetAmounts.getMonthlyAmount(column);
        }
    }
    
    private Currency balance() {
        return accountIds.get()
                .map(this::balance)
                .reduce(new Currency(0), Currency::add);
    }

    private Currency balance(AccountId accountId) {
        return fetcher.fetchBalance(accountId)
                .map(Balance::getBalance)
                .map(Currency::getAmount)
                .map(coefficient::apply)
                .map(Currency::new)
                .orElse(new Currency(0));
    }
}
