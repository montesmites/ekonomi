package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public interface RowWithAccounts extends RowWithAmounts {

  Supplier<Stream<AccountId>> getAccountIds();

  @Override
  default Optional<RowWithAccounts> asRowWithAccounts() {
    return Optional.of(this);
  }
}
