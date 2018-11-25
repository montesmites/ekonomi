package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toMap;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class CompactSectionDecoratorTest {

  @Test
  void fundamentals() {
    var title = "TITLE";
    var values =
        Column.streamMonths().collect(toMap(month -> month, month -> Currency.of(month.ordinal())));
    var header = Header.of(() -> title).add(SHORT_MONTHS_HEADER);
    var body = Body.of(() -> Stream.of(new DefaultRowWithAmounts(title, values::get)));
    var section = Section.of(header, body);
    var decorator = new CompactSectionDecorator();
    var actualSection = decorator.decorate(section);
    var expectedSection =
        new _Section4TestUtil()
            .addBodyRowWithAmounts(title.toUpperCase(), values::get)
            .add(Row.empty());
    expectedSection.assertIsEqualTo(actualSection);
  }
}
