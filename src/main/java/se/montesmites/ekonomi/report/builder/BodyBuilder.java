package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

public class BodyBuilder {

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

  private final Year year;
  private final AmountsFetcher amountsFetcher;
  private List<AccountGroup> accountGroups = new ArrayList<>();
  private boolean materialized = true;

  BodyBuilder(Year year, AmountsFetcher amountsFetcher) {
    this.year = year;
    this.amountsFetcher = amountsFetcher;
  }

  public BodyBuilder accountGroups(List<AccountGroup> accountGroups) {
    this.accountGroups = accountGroups;
    return this;
  }

  public BodyBuilder dematerialize() {
    this.materialized = false;
    return this;
  }

  public Body body() {
    var amountProviders =
        accountGroups
            .stream()
            .map(accountGroup -> AmountsProvider.of(amountsFetcher, year, accountGroup))
            .collect(toList());
    return Body.of(amountProviders::stream);
  }

  boolean isMaterialized() {
    return materialized;
  }
}
