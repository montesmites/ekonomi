package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class DefaultRowWithAccounts implements RowWithAccounts {

    private final CashflowDataFetcher fetcher;
    private final Supplier<Stream<AccountId>> accountIds;
    private final java.time.Year year;
    private final String description;

    public DefaultRowWithAccounts(CashflowDataFetcher fetcher, Supplier<Stream<AccountId>> accountIds, java.time.Year year, String description) {
        this.fetcher = fetcher;
        this.accountIds = accountIds;
        this.year = year;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String formatDescription() {
        return getDescription();
    }

    @Override
    public Supplier<Stream<AccountId>> getAccountIds() {
        return accountIds;
    }

    @Override
    public Currency getMonthlyAmount(Column column) {
        return accountIds.get()
                .map(acc -> getMonthlyAmount(acc, column.getMonth().get()))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    private Currency getMonthlyAmount(AccountId accountId, Month month) {
        return getMonthlyAmount(accountId, YearMonth.of(year.getValue(), month));
    }

    private Currency getMonthlyAmount(AccountId accountId, YearMonth yearMonth) {
        return fetcher.fetchAmount(accountId, yearMonth)
                .map(currency -> currency.getAmount())
                .map(Currency::new)
                .map(amount -> Signedness.NEGATED_SIGN.apply(amount))
                .orElse(new Currency(0));
    }
}
