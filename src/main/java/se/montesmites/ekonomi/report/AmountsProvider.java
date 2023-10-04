package se.montesmites.ekonomi.report;

import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
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
import java.util.stream.Stream;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

@FunctionalInterface
public interface AmountsProvider {

  static AmountsProvider empty() {
    return column -> Optional.of(Amount.ZERO);
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
    return month -> amounts.apply(month).map(Currency::toAmount);
  }

  static AmountsProvider of(Map<Month, Currency> amounts) {
    return month -> Optional.ofNullable(amounts.get(month)).map(Currency::toAmount);
  }

  static AmountsProvider of(String description, Function<Month, Optional<Currency>> amounts) {
    return new AmountsProvider() {
      @Override
      public Optional<Amount> getMonthlyAmount(Month month) {
        return amounts.apply(month).map(Currency::toAmount);
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
            entry(TOTAL, getYearlyTotal().orElse(Amount.ZERO).format()),
            entry(AVERAGE, getAverage().orElse(Amount.ZERO).format()));
    return column ->
        map.getOrDefault(
            column, column.getMonth().flatMap(this::getMonthlyAmount).orElse(Amount.ZERO).format());
  }

  default String formatDescription() {
    return "";
  }

  Optional<Amount> getMonthlyAmount(Month month);

  default Optional<Amount> getYearlyTotal() {
    return Optional.of(
        stream(Month.values())
            .map(this::getMonthlyAmount)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(Amount.ZERO, Amount::add));
  }

  default Optional<Amount> getAverage() {
    record MonthAndAmount(Month month, Optional<Amount> amount) {}
    var amounts =
        Stream.of(Month.values())
            .map(month -> new MonthAndAmount(month, this.getMonthlyAmount(month)))
            .collect(toMap(MonthAndAmount::month, MonthAndAmount::amount));
    var sum =
        amounts.values().stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(Amount.ZERO, Amount::add);
    var count = amounts.values().stream().filter(Optional::isPresent).count();
    var average = count == 0 ? Amount.ZERO : sum.divide(Amount.of(count));
    return Optional.of(average);
  }

  default AmountsProvider self() {
    return this;
  }

  default AmountsProvider negate() {
    var base = this;
    return column -> base.getMonthlyAmount(column).map(Amount::negate);
  }

  default AmountsProvider accumulate(Currency initialBalance) {
    var amounts = doAccumulate(initialBalance);
    return new AmountsProvider() {
      @Override
      public Optional<Amount> getMonthlyAmount(Month month) {
        return Optional.ofNullable(amounts.get(month)).map(Currency::toAmount);
      }

      @Override
      public Optional<Amount> getYearlyTotal() {
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
                    accumulator.add(
                        base.getMonthlyAmount(month)
                            .map(Amount::amount)
                            .map(Currency::from)
                            .orElse(Currency.zero())),
                    Currency::add),
            Currency::add);
    return Map.copyOf(amounts);
  }
}
