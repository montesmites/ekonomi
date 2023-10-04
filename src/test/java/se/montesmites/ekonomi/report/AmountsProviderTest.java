package se.montesmites.ekonomi.report;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.time.Month;
import java.time.Year;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.builder.AmountsFetcherBuilder;

class AmountsProviderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void empty() {
    var exp = AmountsProvider.of(__ -> Optional.of(Currency.zero()));
    var act = AmountsProvider.empty();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void of_function() {
    var row = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var exp =
        stream(Month.values())
            .map(month -> Optional.of(new Currency(month.ordinal()).toAmount()))
            .toList();
    var act = stream(Month.values()).map(row::getMonthlyAmount).toList();
    assertEquals(exp, act);
  }

  @Test
  void of_map() {
    var amounts =
        Map.ofEntries(
            entry(JANUARY, new Currency(100)),
            entry(FEBRUARY, new Currency(100)),
            entry(MARCH, new Currency(100)));
    var exp =
        AmountsProvider.of(
            month ->
                Map.ofEntries(
                        entry(JANUARY, Optional.of(new Currency(100))),
                        entry(FEBRUARY, Optional.of(new Currency(100))),
                        entry(MARCH, Optional.of(new Currency(100))))
                    .getOrDefault(month, Optional.empty()));
    var act = AmountsProvider.of(amounts);
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void of_amountsFetcher_accountGroup() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
                Map.ofEntries(
                    entry(new AccountId(yearId, "1111"), row1),
                    entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var accountGroup = AccountGroup.of("", "\\d\\d\\d\\d");
    var exp =
        AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 300)))
            .asRow()
            .asExtendedString();
    var act = AmountsProvider.of(amountsFetcher, year, accountGroup).asRow().asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_account() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
                Map.ofEntries(
                    entry(new AccountId(yearId, "1111"), row1),
                    entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var account =
        new Account(
            amountsFetcher
                .streamAccountIds(year, accountId -> accountId.id().equals("1111"))
                .findFirst()
                .orElseThrow(),
            "1111",
            AccountStatus.OPEN);
    var exp =
        AmountsProvider.of(
                account.description(),
                month -> row1.getMonthlyAmount(month).map(Amount::amount).map(Currency::from))
            .asRow()
            .asExtendedString();
    var act =
        AmountsProvider.of(amountsFetcher, year, account.accountId(), account.description(), x -> x)
            .asRow()
            .asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void getYearlyTotal() {
    var row = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var sum = stream(Month.values()).mapToInt(Month::ordinal).sum();
    var exp = Optional.of(new Currency(sum)).map(Currency::toAmount);
    var act = row.getYearlyTotal();
    assertEquals(exp, act);
  }

  @Test
  void getAverage() {
    var row = AmountsProvider.of(month -> Optional.of(new Currency((month.ordinal() + 1) * 100)));
    var avg =
        stream(Month.values())
            .mapToInt(month -> (month.ordinal() + 1) * 100)
            .average()
            .orElseThrow();
    var exp = Optional.of(new Currency((int) avg)).map(Currency::toAmount);
    var act = row.getAverage();
    assertEquals(exp, act);
  }

  @Test
  void getAverage_3months() {
    var amounts =
        Map.ofEntries(
            entry(JANUARY, new Currency(100)),
            entry(FEBRUARY, new Currency(100)),
            entry(MARCH, new Currency(100)));
    var row = AmountsProvider.of(amounts);
    var exp = Optional.of(new Currency(100));
    var act = row.getAverage();
    assertEquals(exp.map(Currency::toAmount), act);
  }

  @Test
  void getAverage_withOptionalEmpty() {
    var amounts =
        (Function<Month, Optional<Currency>>)
            month ->
                month == Month.NOVEMBER || month == Month.DECEMBER
                    ? Optional.empty()
                    : Optional.of(new Currency(month.getValue()));
    var row = AmountsProvider.of(amounts);
    var exp = Optional.of(new Currency(6));
    var act = row.getAverage();
    assertEquals(exp.map(Currency::toAmount), act);
  }

  @Test
  void negate() {
    var neutral = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var negated = AmountsProvider.of(month -> Optional.of(new Currency(-month.ordinal() * 100)));
    var act = neutral.negate();
    assertTrue(act.isEquivalentTo(negated));
  }

  @Test
  void of_description() {
    var description = "DESCRIPTION";
    var amountsProvider = AmountsProvider.of(description, __ -> Optional.of(Currency.zero()));
    var row = amountsProvider.asRow();
    var exp =
        Row.of(
                column ->
                    Map.ofEntries(entry(DESCRIPTION, description))
                        .getOrDefault(column, Currency.zero().format()))
            .asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void accumulate() {
    var initialBalance = new Currency(100);
    var expectedAmounts =
        Map.ofEntries(
            entry(JANUARY, new Currency(200)),
            entry(FEBRUARY, new Currency(400)),
            entry(MARCH, new Currency(700)),
            entry(APRIL, new Currency(1100)),
            entry(MAY, new Currency(1600)),
            entry(JUNE, new Currency(2200)),
            entry(JULY, new Currency(2900)),
            entry(AUGUST, new Currency(3700)),
            entry(SEPTEMBER, new Currency(4600)),
            entry(OCTOBER, new Currency(5600)),
            entry(NOVEMBER, new Currency(6700)),
            entry(DECEMBER, new Currency(7900)));
    var exp =
        new AmountsProvider() {
          @Override
          public Optional<Amount> getMonthlyAmount(Month month) {
            return Optional.of(expectedAmounts.get(month)).map(Currency::toAmount);
          }

          @Override
          public String formatDescription() {
            return initialBalance.format();
          }

          @Override
          public Optional<Amount> getYearlyTotal() {
            return Optional.empty();
          }
        };
    var row = AmountsProvider.of(month -> Optional.of(new Currency((month.ordinal() + 1) * 100)));
    var act = row.accumulate(initialBalance);
    assertEquals(exp.asRow().asExtendedString(), act.asRow().asExtendedString());
  }
}
