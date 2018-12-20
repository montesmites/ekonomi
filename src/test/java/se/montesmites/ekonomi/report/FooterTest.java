package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class FooterTest {

  @Test
  void emptyFooter() {
    var footer = Footer.empty();
    var exp = List.<Row>of();
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  @Test
  void of() {
    var amountsProvider = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal())));
    var row = amountsProvider.asRow();
    var footer = Footer.of(row);
    var exp = List.of(Row.of(row::format));
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  private void assertFooters(List<? extends Row> exp, List<? extends Row> act) {
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            assertAll(
                () -> range(0, exp.size()).forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
  }

  @Test
  void of_list() {
    var row1 = Row.title("title1");
    var row2 = Row.title("title2");
    var footer = Footer.of(List.of(row1, row2));
    var exp = List.of(row1, row2);
    var act = footer.stream().collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void asString() {
    var row1 = Row.title("title1");
    var row2 = Row.title("title2");
    var footer = Footer.of(List.of(row1, row2));
    var exp = row1.asString() + "\n" + row2.asString();
    var act = footer.asString("\n");
    assertEquals(exp, act);
  }
}
