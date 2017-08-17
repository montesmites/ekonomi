package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class DefaultRowWithAccountsWithNegatedAmounts implements RowWithAccounts {

    private final RowWithAccounts source;

    public DefaultRowWithAccountsWithNegatedAmounts(RowWithAccounts source) {
        this.source = source;
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public Supplier<Stream<AccountId>> getAccountIds() {
        return source.getAccountIds();
    }

    @Override
    public Currency getMonthlyAmount(Column column) {
        final Currency sourceAmount = source.getMonthlyAmount(column);
        return Signedness.NEGATED_SIGN.apply(sourceAmount);
    }
}
