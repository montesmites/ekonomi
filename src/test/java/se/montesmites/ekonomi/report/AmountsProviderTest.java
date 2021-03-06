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
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.time.Month;
import java.time.Year;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
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
    var row = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal())));
    var exp =
        stream(Month.values())
            .map(month -> Optional.of(Currency.of(month.ordinal())))
            .collect(toList());
    var act = stream(Month.values()).map(row::getMonthlyAmount).collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void of_map() {
    var amounts =
        Map.ofEntries(
            entry(JANUARY, Currency.of(100)),
            entry(FEBRUARY, Currency.of(100)),
            entry(MARCH, Currency.of(100)));
    var exp =
        AmountsProvider.of(
            month ->
                Map.ofEntries(
                    entry(JANUARY, Optional.of(Currency.of(100))),
                    entry(FEBRUARY, Optional.of(Currency.of(100))),
                    entry(MARCH, Optional.of(Currency.of(100))))
                    .getOrDefault(month, Optional.empty()));
    var act = AmountsProvider.of(amounts);
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void of_amountsFetcher_accountGroup() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var accountGroup = AccountGroup.of("", "\\d\\d\\d\\d");
    var exp =
        AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 300)))
            .asRow()
            .asExtendedString();
    var act = AmountsProvider.of(amountsFetcher, year, accountGroup).asRow().asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_account() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var account =
        new Account(
            amountsFetcher
                .streamAccountIds(year, accountId -> accountId.getId().equals("1111"))
                .findFirst()
                .orElseThrow(),
            "1111",
            AccountStatus.OPEN);
    var exp =
        AmountsProvider.of(account.getDescription(), row1::getMonthlyAmount)
            .asRow()
            .asExtendedString();
    var act =
        AmountsProvider.of(
            amountsFetcher, year, account.getAccountId(), account.getDescription(), x -> x)
            .asRow()
            .asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void getYearlyTotal() {
    var row = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal())));
    var sum = stream(Month.values()).mapToInt(Month::ordinal).sum();
    var exp = Optional.of(Currency.of(sum));
    var act = row.getYearlyTotal();
    assertEquals(exp, act);
  }

  @Test
  void getAverage() {
    var row = AmountsProvider.of(month -> Optional.of(Currency.of((month.ordinal() + 1) * 100)));
    var avg =
        stream(Month.values())
            .mapToInt(month -> (month.ordinal() + 1) * 100)
            .average()
            .orElseThrow();
    var exp = Optional.of(Currency.of((int) avg));
    var act = row.getAverage();
    assertEquals(exp, act);
  }

  @Test
  void getAverage_3months() {
    var amounts =
        Map.ofEntries(
            entry(JANUARY, Currency.of(100)),
            entry(FEBRUARY, Currency.of(100)),
            entry(MARCH, Currency.of(100)));
    var row = AmountsProvider.of(amounts);
    var exp = Optional.of(Currency.of(100));
    var act = row.getAverage();
    assertEquals(exp, act);
  }

  @Test
  void getAverage_withOptionalEmpty() {
    var amounts =
        (Function<Month, Optional<Currency>>)
            month ->
                month == Month.NOVEMBER || month == Month.DECEMBER
                    ? Optional.empty()
                    : Optional.of(Currency.of(month.getValue()));
    var row = AmountsProvider.of(amounts);
    var exp = Optional.of(Currency.of(6));
    var act = row.getAverage();
    assertEquals(exp, act);
  }

  @Test
  void negate() {
    var neutral = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var negated = AmountsProvider.of(month -> Optional.of(Currency.of(-month.ordinal() * 100)));
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
    var initialBalance = Currency.of(100);
    var expectedAmounts =
        Map.ofEntries(
            entry(JANUARY, Currency.of(200)),
            entry(FEBRUARY, Currency.of(400)),
            entry(MARCH, Currency.of(700)),
            entry(APRIL, Currency.of(1100)),
            entry(MAY, Currency.of(1600)),
            entry(JUNE, Currency.of(2200)),
            entry(JULY, Currency.of(2900)),
            entry(AUGUST, Currency.of(3700)),
            entry(SEPTEMBER, Currency.of(4600)),
            entry(OCTOBER, Currency.of(5600)),
            entry(NOVEMBER, Currency.of(6700)),
            entry(DECEMBER, Currency.of(7900)));
    var exp =
        new AmountsProvider() {
          @Override
          public Optional<Currency> getMonthlyAmount(Month month) {
            return Optional.of(expectedAmounts.get(month));
          }

          @Override
          public String formatDescription() {
            return initialBalance.format();
          }

          @Override
          public Optional<Currency> getYearlyTotal() {
            return Optional.empty();
          }
        };
    var row = AmountsProvider.of(month -> Optional.of(Currency.of((month.ordinal() + 1) * 100)));
    var act = row.accumulate(initialBalance);
    assertEquals(exp.asRow().asExtendedString(), act.asRow().asExtendedString());
  }
}
