package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;

class BodyFromAccountsBuilder {

  static BodyFromAccountsBuilder empty() {
    return new BodyFromAccountsBuilder(AmountsFetcher.empty(), Year.now());
  }

  static BodyFromAccountsBuilder of(AmountsFetcher amountsFetcher, Year year) {
    return new BodyFromAccountsBuilder(amountsFetcher, year);
  }

  private final AmountsFetcher amountsFetcher;
  private final Year year;
  private List<Account> accounts = List.of();
  private Function<Account, String> descriptionProcessor = Account::getDescription;
  private UnaryOperator<AmountsProvider> amountsProviderProcessor = x -> x;

  private BodyFromAccountsBuilder(AmountsFetcher amountsFetcher, Year year) {
    this.amountsFetcher = amountsFetcher;
    this.year = year;
  }

  BodyFromAccountsBuilder accounts(List<Account> accounts) {
    this.accounts = List.copyOf(accounts);
    return this;
  }

  BodyFromAccountsBuilder descriptionProcessor(
      Function<Account, String> descriptionProcessor) {
    this.descriptionProcessor = descriptionProcessor;
    return this;
  }

  BodyFromAccountsBuilder amountsProviderProcessor(
      UnaryOperator<AmountsProvider> amountsProviderProcessor) {
    this.amountsProviderProcessor = amountsProviderProcessor;
    return this;
  }

  List<AmountsProvider> getAmountsProviders() {
    return accounts
        .stream()
        .map(
            account ->
                AmountsProvider.of(
                    amountsFetcher,
                    year,
                    account.getAccountId(),
                    descriptionProcessor.apply(account),
                    amountsProviderProcessor))
        .collect(toList());
  }
}
