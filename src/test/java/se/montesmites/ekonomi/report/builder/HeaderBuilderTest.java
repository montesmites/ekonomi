package se.montesmites.ekonomi.report.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;

class HeaderBuilderTest {

  @Test
  void empty() {
    var headerBuilder = HeaderBuilder.empty();
    var exp = Header.empty();
    var act = headerBuilder.header();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void titleOnly() {
    var headerBuilder = new HeaderBuilder();
    var title = "title";
    var exp = Header.of(Row.title(title));
    var act = headerBuilder.title(title).header();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void monthsOnly() {
    var headerBuilder = new HeaderBuilder();
    var exp = Header.of(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var act = headerBuilder.months().header();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void titleAndMonths() {
    var headerBuilder = new HeaderBuilder();
    var title = "title";
    var exp = Header.of(List.of(Row.title(title), Row.descriptionWithMonths("", Row.SHORT_MONTHS)));
    var act = headerBuilder.title(title).months().header();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
