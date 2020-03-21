package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class BodyTest {

  @Test
  void emptyBody() {
    var exp = Body.of(Stream::empty);
    var act = Body.empty();
    assertBodys(exp, act);
  }

  @Test
  void oneRowWithAmounts() {
    var row = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var exp = Body.of(row);
    var act = Body.of(row);
    assertBodys(exp, act);
  }

  @Test
  void twoRowsWithAmounts() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var exp = Body.of(() -> Stream.of(row1, row2));
    var act = Body.of(row1).add(row2);
    assertBodys(exp, act);
  }

  @Test
  void streamOfRowsWithAmounts() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var exp = Body.of(() -> Stream.of(row1, row2));
    var act = Body.of(() -> Stream.of(row1, row2));
    assertBodys(exp, act);
  }

  @Test
  void of_list() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var exp = List.of(row1, row2);
    var act = Body.of(List.of(row1, row2)).stream().collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void negate() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp = Body.of(() -> Stream.of(row1.negate(), row2.negate()));
    var act = body.negate();
    assertBodys(exp, act);
  }

  private void assertBodys(Body expected, Body actutal) {
    var exp = expected.stream().collect(toList());
    var act = actutal.stream().collect(toList());
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            assertAll(
                () -> range(0, exp.size()).forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
  }

  @Test
  void asString() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal())));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var exp = row1.asRow().asExtendedString() + "\n" + row2.asRow().asExtendedString();
    var act = Body.of(List.of(row1, row2)).asString("\n");
    assertEquals(exp, act);
  }
}
