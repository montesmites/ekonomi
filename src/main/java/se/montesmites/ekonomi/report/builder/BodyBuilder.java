package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

public class BodyBuilder {

  private final Year year;
  private final AmountsFetcher amountsFetcher;
  private List<AmountsProvider> amountsProviders = new ArrayList<>();

  BodyBuilder(Year year, AmountsFetcher amountsFetcher) {
    this.year = year;
    this.amountsFetcher = amountsFetcher;
  }

  public static BodyBuilder empty() {
    return new BodyBuilder(null, null) {
      @Override
      public BodyBuilder accountGroups(List<AccountGroup> accountGroups) {
        return this;
      }

      @Override
      public Body body() {
        return Body.empty();
      }
    };
  }

  public BodyBuilder accountGroups(List<AccountGroup> accountGroups) {
    this.amountsProviders =
        accountGroups.stream()
            .map(accountGroup -> AmountsProvider.of(amountsFetcher, year, accountGroup))
            .collect(toList());
    return this;
  }

  BodyBuilder accounts(UnaryOperator<BodyFromAccountsBuilder> body) {
    var bodyFromAccountsBuilder = BodyFromAccountsBuilder.of(amountsFetcher, year);
    body.apply(bodyFromAccountsBuilder);
    this.amountsProviders = bodyFromAccountsBuilder.getAmountsProviders();
    return this;
  }

  public Body body() {
    return Body.of(this.amountsProviders::stream);
  }
}
