package se.montesmites.ekonomi.report;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class AggregateTest {

  @Test
  void aggregate() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp =
        AmountsProvider.of(
            month -> Optional.of(new Currency(month.ordinal() * 100 + month.ordinal())));
    var act = Aggregate.of(body);
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void aggregate_threeMonths() {
    var row1 =
        AmountsProvider.of(
            Map.ofEntries(
                entry(JANUARY, new Currency(1000)),
                entry(FEBRUARY, new Currency(2000)),
                entry(MARCH, new Currency(3000))));
    var row2 =
        AmountsProvider.of(
            Map.ofEntries(
                entry(JANUARY, new Currency(100)),
                entry(FEBRUARY, new Currency(200)),
                entry(MARCH, new Currency(300))));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp =
        AmountsProvider.of(
            Map.ofEntries(
                entry(JANUARY, new Currency(1100)),
                entry(FEBRUARY, new Currency(2200)),
                entry(MARCH, new Currency(3300))));
    var act = Aggregate.of(body);
    assertTrue(act.isEquivalentTo(exp));
  }
}
