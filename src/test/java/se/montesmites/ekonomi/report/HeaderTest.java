package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.List;
import java.util.Map;
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
  void headerRow() {
    var header = Header.of(SHORT_MONTHS_HEADER);
    var exp = List.of(SHORT_MONTHS_HEADER);
    var act = header.stream().collect(toList());
    assertHeaders(exp, act);
  }

  @Test
  void bothTitleAndHeader() {
    var header = Header.of(Row.of(Map.of(DESCRIPTION, "TITLE"))).add(SHORT_MONTHS_HEADER);
    var exp = List.of(Row.of(Map.of(DESCRIPTION, "TITLE")), SHORT_MONTHS_HEADER);
    var act = header.stream().collect(toList());
    assertHeaders(exp, act);
  }

  @Test
  void combineTitleAndHeader() {
    var titleRow = Row.of(Map.of(DESCRIPTION, "TITLE"));
    var headerRow = (HeaderRow) SHORT_MONTHS_HEADER;
    var header = Header.of(headerRow.merge(DESCRIPTION, titleRow));
    var exp = List.of(headerRow.merge(DESCRIPTION, titleRow));
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
}
