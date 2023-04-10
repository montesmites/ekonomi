package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.List;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.report.AccountDescriptor;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;

class BodyFromAccountsBuilder {

  private final AmountsFetcher amountsFetcher;
  private final Year year;
  private List<Account> accounts = List.of();
  private AccountDescriptor accountDescriptor = AccountDescriptor.accountDescription();
  private UnaryOperator<AmountsProvider> amountsProviderProcessor = x -> x;

  private BodyFromAccountsBuilder(AmountsFetcher amountsFetcher, Year year) {
    this.amountsFetcher = amountsFetcher;
    this.year = year;
  }

  static BodyFromAccountsBuilder empty() {
    return new BodyFromAccountsBuilder(AmountsFetcher.empty(), Year.now());
  }

  static BodyFromAccountsBuilder of(AmountsFetcher amountsFetcher, Year year) {
    return new BodyFromAccountsBuilder(amountsFetcher, year);
  }

  BodyFromAccountsBuilder accounts(List<Account> accounts) {
    this.accounts = List.copyOf(accounts);
    return this;
  }

  BodyFromAccountsBuilder accountDescriptor(AccountDescriptor accountDescriptor) {
    this.accountDescriptor = accountDescriptor;
    return this;
  }

  BodyFromAccountsBuilder amountsProviderProcessor(
      UnaryOperator<AmountsProvider> amountsProviderProcessor) {
    this.amountsProviderProcessor = amountsProviderProcessor;
    return this;
  }

  List<AmountsProvider> getAmountsProviders() {
    return accounts.stream()
        .map(
            account ->
                AmountsProvider.of(
                    amountsFetcher,
                    year,
                    account.accountId(),
                    accountDescriptor.describe(account),
                    amountsProviderProcessor))
        .collect(toList());
  }
}
