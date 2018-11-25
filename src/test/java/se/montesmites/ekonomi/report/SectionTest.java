package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class SectionTest {

  @Test
  void empty() {
    var exp = Section.of(Header.empty(), Body.empty(), Footer.empty());
    var act = Section.empty();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void stream() {
    var header = Header.of(() -> "title");
    var body = Body.of(RowWithAmounts.of(column -> Currency.of(column.ordinal())));
    var footer = Footer.of(body.aggregate());
    var exp =
        Stream.of(header.stream(), body.stream(), footer.stream())
            .flatMap(row -> row)
            .collect(toList());
    var act = Section.of(header, body, footer).stream().collect(toList());
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            range(0, exp.size())
                .forEach(
                    i -> assertTrue(exp.get(i).isEquivalentTo(act.get(i)), Integer.toString(i))));
  }

  @Test
  void of() {
    var header = Header.of(() -> "title");
    var body = Body.of(RowWithAmounts.of(column -> Currency.of(column.ordinal())));
    var footer = Footer.of(body.aggregate());
    var exp =
        new Section() {
          @Override
          public Header header() {
            return header;
          }

          @Override
          public Body body() {
            return body;
          }

          @Override
          public Footer footer() {
            return footer;
          }
        };
    var act = Section.of(header, body, footer);
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void compact() {
    var description = "DESCRIPTION";
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var body = Body.of(() -> Stream.of(row));
    var exp =
        Section.of(
            Header.empty(),
            Body.empty(),
            Footer.of(Row.of(column -> column == DESCRIPTION ? description : row.format(column))));
    var act = Section.compact(description, body);
    assertTrue(act.isEquivalentTo(exp));
  }
}
