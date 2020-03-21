package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Account;

@FunctionalInterface
public interface AccountDescriptor {

  static AccountDescriptor accountDescription() {
    return Account::description;
  }

  static AccountDescriptor accountIdConcatAccountDescriptionWithMaxLength(int maxLength) {
    //noinspection Convert2Lambda
    return new AccountDescriptor() {
      @Override
      public String describe(Account account) {
        var accountId = account.accountId().id();
        var delimiter = " ";
        var description = account.description();
        var prefix = accountId + delimiter;
        var full = prefix + description;
        var chopAt = Math.min(full.length(), maxLength);
        return full.substring(0, chopAt);
      }
    };
  }

  String describe(Account account);
}
