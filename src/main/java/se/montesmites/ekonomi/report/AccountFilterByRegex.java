package se.montesmites.ekonomi.report;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import se.montesmites.ekonomi.model.AccountId;

public class AccountFilterByRegex implements Predicate<AccountId> {

  public static AccountFilterByRegex of(AccountGroup accountGroup) {
    return AccountFilterByRegex.of(accountGroup.regex());
  }

  public static AccountFilterByRegex of(String regex) {
    return new AccountFilterByRegex(regex);
  }

  private final Pattern pattern;

  private AccountFilterByRegex(String regex) {
    this.pattern = Pattern.compile(regex);
  }

  @Override
  public boolean test(AccountId accountId) {
    return pattern.matcher(accountId.id()).matches();
  }
}
