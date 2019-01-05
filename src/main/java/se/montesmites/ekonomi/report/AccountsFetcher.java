package se.montesmites.ekonomi.report;

import java.util.Optional;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;

@FunctionalInterface
public interface AccountsFetcher {

  static AccountsFetcher empty() {
    return __ -> Optional.empty();
  }

  Optional<Account> getAccount(AccountId accountId);
}
