package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.AccountId;

public class BodyRow implements Row {
    private final AccountId accountId;
    
    public BodyRow(AccountId accountId) {
        this.accountId = accountId;
    }
}
