package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

class BodyBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void empty() {
    var bodyBuilder = BodyBuilder.empty();
    var exp = Body.empty();
    var act = bodyBuilder.body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void fromAccountGroups() {
    var description1 = "description1";
    var description2 = "description2";
    var accountGroups =
        List.of(AccountGroup.of(description1, "1111"), AccountGroup.of(description2, "2222"));
    var row1 =
        AmountsProvider.of(description1, month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(description2, month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var bodyBuilder = new BodyBuilder(year, amountsFetcher);
    var exp = Body.of(List.of(row1, row2));
    var act = bodyBuilder.accountGroups(accountGroups).body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void fromAccounts() {
    var accountId1 = new AccountId(yearId, "1111");
    var accountId2 = new AccountId(yearId, "2222");
    var account1 = new Account(accountId1, accountId1.id(), AccountStatus.OPEN);
    var account2 = new Account(accountId2, accountId2.id(), AccountStatus.OPEN);
    var row1 =
        AmountsProvider.of(
            account1.description(), month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(
            account2.description(), month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(accountId1, row1), entry(accountId2, row2)))
            .amountsFetcher();
    var bodyBuilder = new BodyBuilder(year, amountsFetcher);
    var exp = Body.of(List.of(row1, row2));
    var act = bodyBuilder.accounts(body -> body.accounts(List.of(account1, account2))).body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
