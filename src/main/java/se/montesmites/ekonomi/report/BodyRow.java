package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.Optional;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class BodyRow implements Row {
    private final CashflowDataFetcher fetcher;
    private final AccountId accountId;
    
    public BodyRow(CashflowDataFetcher fetcher, AccountId accountId) {
        this.fetcher = fetcher;
        this.accountId = accountId;
    }

    public AccountId getAccountId() {
        return accountId;
    }
    
    public Optional<Currency> getMonthlyAmount(YearMonth yearMonth) {
        return fetcher.fetchAmount(accountId, yearMonth);
    }
}
