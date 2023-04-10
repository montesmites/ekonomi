package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.YearId;

class AccountFilterByRegexTest {

  private static final YearId yearId = new YearId("2012");

  private static final AccountId ACCOUNT_ID_3000 = new AccountId(yearId, "3000");
  private static final AccountId ACCOUNT_ID_3100 = new AccountId(yearId, "3100");
  private static final AccountId ACCOUNT_ID_3200 = new AccountId(yearId, "3200");
  private static final AccountId ACCOUNT_ID_3300 = new AccountId(yearId, "3300");
  private static final AccountId ACCOUNT_ID_3400 = new AccountId(yearId, "3400");
  private static final AccountId ACCOUNT_ID_3500 = new AccountId(yearId, "3500");
  private static final AccountId ACCOUNT_ID_3600 = new AccountId(yearId, "3600");
  private static final AccountId ACCOUNT_ID_3700 = new AccountId(yearId, "3700");
  private static final AccountId ACCOUNT_ID_3800 = new AccountId(yearId, "3800");
  private static final AccountId ACCOUNT_ID_3900 = new AccountId(yearId, "3900");

  @Test
  void of_accountGroup() {
    var regex = "3([1-5]|[7-9])\\d\\d";
    var description = "account-group";
    var accountGroup = AccountGroup.of(description, regex);
    assertAll(
        () -> assertEquals(description, accountGroup.description()),
        () -> assertEquals(regex, accountGroup.regex()));
  }

  @Test
  void test() {
    var regex = "3([1-5]|[7-9])\\d\\d";
    var filter = AccountFilterByRegex.of(regex);
    var exp =
        Set.of(
            ACCOUNT_ID_3100,
            ACCOUNT_ID_3200,
            ACCOUNT_ID_3300,
            ACCOUNT_ID_3400,
            ACCOUNT_ID_3500,
            ACCOUNT_ID_3700,
            ACCOUNT_ID_3800,
            ACCOUNT_ID_3900);
    var act =
        Stream.of(
                ACCOUNT_ID_3000,
                ACCOUNT_ID_3100,
                ACCOUNT_ID_3200,
                ACCOUNT_ID_3300,
                ACCOUNT_ID_3400,
                ACCOUNT_ID_3500,
                ACCOUNT_ID_3600,
                ACCOUNT_ID_3700,
                ACCOUNT_ID_3800,
                ACCOUNT_ID_3900)
            .filter(filter)
            .collect(toSet());
    assertEquals(exp, act);
  }
}
