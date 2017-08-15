package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public interface BodyRow extends Row, RowWithAmounts {

    public Supplier<Stream<AccountId>> getAccountIds();
}
