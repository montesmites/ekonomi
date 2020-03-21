package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;
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
import se.montesmites.ekonomi.report.AmountsProvider;

class BodyFromAccountsBuilderTest {

  @Test
  void empty() {
    var bodyFromAccountsBuilder = BodyFromAccountsBuilder.empty();
    var exp = List.of();
    var act = bodyFromAccountsBuilder.getAmountsProviders();
    assertEquals(exp, act);
  }

  @Test
  void accounts() {
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var description1 = "1111";
    var description2 = "2222";
    var row1 =
        AmountsProvider.of(description1, month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(description2, month -> Optional.of(new Currency(month.ordinal() * 200)));
    var accountId1 = new AccountId(yearId, "1111");
    var accountId2 = new AccountId(yearId, "2222");
    var account1 = new Account(accountId1, description1, AccountStatus.OPEN);
    var account2 = new Account(accountId2, description2, AccountStatus.OPEN);
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(accountId1, row1), entry(accountId2, row2)))
            .amountsFetcher();
    var bodyFromAccountsBuilder =
        BodyFromAccountsBuilder.of(amountsFetcher, year).accounts(List.of(account1, account2));
    var exp =
        List.of(row1, row2)
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    var act =
        bodyFromAccountsBuilder
            .getAmountsProviders()
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void accountsAndAccountDescriptor() {
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var description1 = "1111";
    var description2 = "2222";
    var row1 =
        AmountsProvider.of(
            description1 + description1, month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(
            description2 + description2, month -> Optional.of(new Currency(month.ordinal() * 200)));
    var accountId1 = new AccountId(yearId, "1111");
    var accountId2 = new AccountId(yearId, "2222");
    var account1 = new Account(accountId1, description1, AccountStatus.OPEN);
    var account2 = new Account(accountId2, description2, AccountStatus.OPEN);
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(accountId1, row1), entry(accountId2, row2)))
            .amountsFetcher();
    var bodyFromAccountsBuilder =
        BodyFromAccountsBuilder.of(amountsFetcher, year)
            .accounts(List.of(account1, account2))
            .accountDescriptor(account -> account.description() + account.description());
    var exp =
        List.of(row1, row2)
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    var act =
        bodyFromAccountsBuilder
            .getAmountsProviders()
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void accountsAndAccountDescriptorAndAmountsProviderProcessor() {
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var description1 = "1111";
    var description2 = "2222";
    var row1 =
        AmountsProvider.of(
            description1 + description1, month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(
            description2 + description2, month -> Optional.of(new Currency(month.ordinal() * 200)));
    var accountId1 = new AccountId(yearId, "1111");
    var accountId2 = new AccountId(yearId, "2222");
    var account1 = new Account(accountId1, description1, AccountStatus.OPEN);
    var account2 = new Account(accountId2, description2, AccountStatus.OPEN);
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(accountId1, row1), entry(accountId2, row2)))
            .amountsFetcher();
    var bodyFromAccountsBuilder =
        BodyFromAccountsBuilder.of(amountsFetcher, year)
            .accounts(List.of(account1, account2))
            .accountDescriptor(account -> account.description() + account.description())
            .amountsProviderProcessor(AmountsProvider::negate);
    var exp =
        List.of(row1.negate(), row2.negate())
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    var act =
        bodyFromAccountsBuilder
            .getAmountsProviders()
            .stream()
            .map(amountsProvider -> amountsProvider.asRow().asExtendedString())
            .collect(toList());
    assertEquals(exp, act);
  }
}
