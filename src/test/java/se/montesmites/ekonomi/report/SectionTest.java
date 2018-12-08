package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.Map;
import java.util.Optional;
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
    var header = Header.of(Row.of(Map.of(DESCRIPTION, "TITLE")));
    var body = Body.of(AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal()))));
    var footer = Footer.of(body.aggregate("").asRow());
    var exp =
        Stream.of(header.stream(), body.stream().map(AmountsProvider::asRow), footer.stream())
            .flatMap(row -> row)
            .collect(toList());
    var act = Section.of(header, body, footer).stream().collect(toList());
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            range(0, exp.size())
                .forEach(
                    i ->
                        assertEquals(
                            exp.get(i).asString(), act.get(i).asString(), Integer.toString(i))));
  }

  @Test
  void of() {
    var header = Header.of(Row.of(Map.of(DESCRIPTION, "TITLE")));
    var body = Body.of(AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal()))));
    var footer = Footer.of(body.aggregate("").asRow());
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
}
