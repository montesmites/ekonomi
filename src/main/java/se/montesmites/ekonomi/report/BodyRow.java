package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public interface BodyRow extends Row {

    public Currency getMonthlyAmount(Column column);

    public Currency getYearlyTotal();

    public Supplier<Stream<AccountId>> getAccountIds();
}
