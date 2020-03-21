package se.montesmites.ekonomi.report;

import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

@FunctionalInterface
public interface AmountsProvider {

  static AmountsProvider empty() {
    return column -> Optional.of(Currency.zero());
  }

  static AmountsProvider of(AmountsFetcher amountsFetcher, Year year, AccountGroup accountGroup) {
    var amountsProvider =
        AmountsProvider.of(
            accountGroup.description(),
            month ->
                !amountsFetcher.touchedMonths(year).contains(month)
                    ? Optional.empty()
                    : Optional.of(
                        amountsFetcher
                            .streamAccountIds(year, AccountFilterByRegex.of(accountGroup.regex()))
                            .map(accountId -> fetchAmount(amountsFetcher, year, month, accountId))
                            .reduce(Currency.zero(), Currency::add)));
    return accountGroup.postProcessor().apply(amountsProvider);
  }

  static AmountsProvider of(
      AmountsFetcher amountsFetcher,
      Year year,
      AccountId accountId,
      String description,
      UnaryOperator<AmountsProvider> postProcessor) {
    var amountsProvider =
        AmountsProvider.of(
            description,
            month ->
                !amountsFetcher.touchedMonths(year).contains(month)
                    ? Optional.empty()
                    : Optional.of(fetchAmount(amountsFetcher, year, month, accountId)));
    return postProcessor.apply(amountsProvider);
  }

  private static Currency fetchAmount(
      AmountsFetcher amountsFetcher, Year year, Month month, AccountId accountId) {
    return amountsFetcher
        .fetchAmount(accountId, YearMonth.of(year.getValue(), month))
        .map(Currency::amount)
        .map(Currency::new)
        .map(Currency::negate)
        .orElse(Currency.zero());
  }

  static AmountsProvider of(Function<Month, Optional<Currency>> amounts) {
    return amounts::apply;
  }

  static AmountsProvider of(Map<Month, Currency> amounts) {
    return month -> Optional.ofNullable(amounts.get(month));
  }

  static AmountsProvider of(String description, Function<Month, Optional<Currency>> amounts) {
    return new AmountsProvider() {
      @Override
      public Optional<Currency> getMonthlyAmount(Month month) {
        return amounts.apply(month);
      }

      @Override
      public String formatDescription() {
        return description;
      }
    };
  }

  default Row asRow() {
    var map =
        Map.ofEntries(
            entry(DESCRIPTION, formatDescription()),
            entry(TOTAL, getYearlyTotal().orElse(Currency.zero()).format()),
            entry(AVERAGE, getAverage().orElse(Currency.zero()).format()));
    return column ->
        map.getOrDefault(
            column,
            column.getMonth().flatMap(this::getMonthlyAmount).orElse(Currency.zero()).format());
  }

  default String formatDescription() {
    return "";
  }

  Optional<Currency> getMonthlyAmount(Month month);

  default Optional<Currency> getYearlyTotal() {
    return Optional.of(
        stream(Month.values())
            .map(this::getMonthlyAmount)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(Currency.zero(), Currency::add));
  }

  default Optional<Currency> getAverage() {
    var average =
        stream(Month.values())
            .map(this::getMonthlyAmount)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .mapToLong(Currency::amount)
            .average()
            .orElse(0);
    return Optional.of(new Currency(Math.round(average)));
  }

  default AmountsProvider self() {
    return this;
  }

  default AmountsProvider negate() {
    var base = this;
    return column -> base.getMonthlyAmount(column).map(Currency::negate);
  }

  default AmountsProvider accumulate(Currency initialBalance) {
    var amounts = doAccumulate(initialBalance);
    return new AmountsProvider() {
      @Override
      public Optional<Currency> getMonthlyAmount(Month month) {
        return Optional.ofNullable(amounts.get(month));
      }

      @Override
      public Optional<Currency> getYearlyTotal() {
        return Optional.empty();
      }

      @Override
      public String formatDescription() {
        return initialBalance.format();
      }
    };
  }

  default boolean isEquivalentTo(AmountsProvider that) {
    return stream(Month.values()).allMatch(columnIsEquivalentPredicate(that))
        && this.getAverage().equals(that.getAverage())
        && this.getYearlyTotal().equals(that.getYearlyTotal());
  }

  private Predicate<Month> columnIsEquivalentPredicate(AmountsProvider that) {
    return month -> this.getMonthlyAmount(month).equals(that.getMonthlyAmount(month));
  }

  private Map<Month, Currency> doAccumulate(Currency initial) {
    var base = this;
    var amounts = new EnumMap<Month, Currency>(Month.class);
    stream(Month.values())
        .filter(month -> base.getMonthlyAmount(month).isPresent())
        .reduce(
            initial,
            (accumulator, month) ->
                amounts.merge(
                    month,
                    accumulator.add(base.getMonthlyAmount(month).orElse(Currency.zero())),
                    Currency::add),
            Currency::add);
    return Map.copyOf(amounts);
  }
}
