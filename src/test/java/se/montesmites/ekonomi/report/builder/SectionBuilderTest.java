package se.montesmites.ekonomi.report.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.builder.SectionBuilder.headerBuilder;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

class SectionBuilderTest {

  @Test
  void header() {
    var sectionBuilder = new SectionBuilder();
    var header = Header.of(Row.title("title"));
    var exp = header.asString("\n");
    var act = sectionBuilder.header(headerBuilder().title("title")).getHeader().asString("\n");
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

  @Test
  void section() {
    var title = Row.title("title");
    var body1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var body2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var sectionBuilder = new SectionBuilder();
    var header = Header.of(title);
    var body = Body.of(List.of(body1, body2));
    var footer = Footer.of(title);
    var exp = Section.of(header, body, footer).asString("\n");
    var act =
        sectionBuilder
            .header(headerBuilder().title("title"))
            .body(body)
            .footer(footer)
            .section()
            .asString("\n");
    assertEquals(exp, act);
  }
}
