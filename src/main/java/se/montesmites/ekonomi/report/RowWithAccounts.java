package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.AccountId;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface RowWithAccounts extends RowWithAmounts {

    Supplier<Stream<AccountId>> getAccountIds();

    @Override
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }

    @Override
    default Optional<RowWithAccounts> asRowWithAccounts() {
        return Optional.of(this);
    }
}
