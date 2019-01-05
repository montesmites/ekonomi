package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.YearId;

class AccountDescriptorTest {

  @Test
  void accountDescription() {
    var description = "description";
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var accountDescriptor = AccountDescriptor.accountDescription();
    var accountid = new AccountId(yearId, "1111");
    var account = new Account(accountid, description, AccountStatus.OPEN);
    var exp = description + "";
    var act = accountDescriptor.describe(account);
    assertEquals(exp, act);
  }

  @Test
  void accountId_accountDescription_withMaxLength() {
    var description = "description";
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var accountDescriptor = AccountDescriptor.accountIdConcatAccountDescriptionWithMaxLength(10);
    var accountid = new AccountId(yearId, "1111");
    var account = new Account(accountid, description, AccountStatus.OPEN);
    var exp = "1111 " + description.substring(0, 5);
    var act = accountDescriptor.describe(account);
    assertEquals(exp, act);
  }
}
