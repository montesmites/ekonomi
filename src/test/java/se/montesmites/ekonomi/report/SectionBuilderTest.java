package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class SectionBuilderTest {

  @Test
  void header() {
    var sectionBuilder = new SectionBuilder();
    var header = Header.of(Row.title("title"));
    var exp = header.asString("\n");
    var act = sectionBuilder.header(header).getHeader().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void body() {
    var sectionBuilder = new SectionBuilder();
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var body = Body.of(List.of(row1, row2));
    var exp = body.asString("\n");
    var act = sectionBuilder.body(body).getBody().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void footer() {
    var sectionBuilder = new SectionBuilder();
    var row = Row.title("title");
    var footer = Footer.of(row);
    var exp = footer.asString("\n");
    var act = sectionBuilder.footer(footer).getFooter().asString("\n");
    assertEquals(exp, act);
  }
}
