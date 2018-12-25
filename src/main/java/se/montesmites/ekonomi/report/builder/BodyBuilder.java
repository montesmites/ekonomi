package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
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

      @Override
      public AmountsProvider buildAmountsProvider(AccountGroup accountGroup) {
        return AmountsProvider.empty();
      }
    };
  }

  private final Year year;
  private final AmountFetcher amountFetcher;
  private List<AccountGroup> accountGroups = new ArrayList<>();
  private boolean materialized = true;

  BodyBuilder(Year year, AmountFetcher amountFetcher) {
    this.year = year;
    this.amountFetcher = amountFetcher;
  }

  public BodyBuilder accountGroups(List<AccountGroup> accountGroups) {
    this.accountGroups = accountGroups;
    return this;
  }

  BodyBuilder dematerialize() {
    this.materialized = false;
    return this;
  }

  public Body body() {
    var amountProviders = accountGroups.stream().map(this::buildAmountsProvider).collect(toList());
    return Body.of(amountProviders::stream);
  }

  boolean isMaterialized() {
    return materialized;
  }

  public AmountsProvider buildAmountsProvider(AccountGroup accountGroup) {
    var accountIds =
        amountFetcher
            .streamAccountIds(year, AccountFilterByRegex.of(accountGroup.regex()))
            .collect(toList());
    var row =
        new AmountsProvider() {
          @Override
          public Optional<Currency> getMonthlyAmount(Month month) {
            var yearMonth = YearMonth.of(year.getValue(), month);
            var year = Year.of(yearMonth.getYear());
            var sum =
                (Supplier<Currency>)
                    () ->
                        accountIds
                            .stream()
                            .map(
                                accountId ->
                                    amountFetcher
                                        .fetchAmount(accountId, yearMonth)
                                        .map(Currency::getAmount)
                                        .map(Currency::of)
                                        .map(Currency::negate)
                                        .orElse(Currency.zero()))
                            .reduce(Currency.zero(), Currency::add);
            return amountFetcher.touchedMonths(year).contains(month)
                ? Optional.of(sum.get())
                : Optional.empty();
          }

          @Override
          public String formatDescription() {
            return accountGroup.description();
          }
        };
    return accountGroup.postProcessor().apply(row);
  }
}
