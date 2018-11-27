package se.montesmites.ekonomi.report;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public class AccountFilterByRegex implements AccountFilter {

  private final Pattern pattern;

  public AccountFilterByRegex(String regex) {
    this.pattern = Pattern.compile(regex);
  }

  @Override
  public Stream<AccountId> filter(Stream<AccountId> accountIds) {
    return accountIds.filter(a -> pattern.matcher(a.getId()).matches()).distinct();
  }
}
