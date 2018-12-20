package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Row.SHORT_MONTHS;

import java.util.List;
import org.junit.jupiter.api.Test;

class HeaderTest {

  @Test
  void emptyHeader() {
    var header = Header.empty();
    var exp = List.<Row>of();
    var act = header.stream().collect(toList());
    assertHeaders(exp, act);
  }

  @Test
  void oneRow() {
    var title = Row.title("title");
    var header = Header.of(title);
    var exp = List.of(title);
    var act = header.stream().collect(toList());
    assertHeaders(exp, act);
  }

  @Test
  void twoRows() {
    var title = Row.title("title");
    var description = "description";
    var descriptionWithMonths = Row.descriptionWithMonths(description, SHORT_MONTHS);
    var header = Header.of(title).add(descriptionWithMonths);
    var exp = List.of(title, descriptionWithMonths);
    var act = header.stream().collect(toList());
    assertHeaders(exp, act);
  }

  private void assertHeaders(List<? extends Row> exp, List<? extends Row> act) {
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
    var exp = List.of(row1, row2);
    var act = Header.of(List.of(row1, row2)).stream().collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void asString() {
    var row1 = Row.title("title1");
    var row2 = Row.title("title2");
    var header = Header.of(List.of(row1, row2));
    var exp = row1.asString() + "\n" + row2.asString();
    var act = header.asString("\n");
    assertEquals(exp, act);
  }
}
