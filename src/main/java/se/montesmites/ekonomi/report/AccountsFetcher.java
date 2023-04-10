package se.montesmites.ekonomi.report;

import java.util.Optional;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;

@FunctionalInterface
public interface AccountsFetcher {

  static AccountsFetcher empty() {
    return __ -> Optional.empty();
  }

  static AccountsFetcher self(YearId yearId) {
    return accountId ->
        Optional.of(
            new Account(new AccountId(yearId, accountId.id()), accountId.id(), AccountStatus.OPEN));
  }

  Optional<Account> getAccount(AccountId accountId);
}
