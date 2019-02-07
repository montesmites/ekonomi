package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class ReportTest {

  @Test
  void render() {
    var header = Header.of(Row.title("header"));
    var body = Body.of(AmountsProvider.of(month -> Optional.of(Currency.of(month.getValue()))));
    var footer = Footer.of(Row.title("footer"));
    var section = Section.of(header, body, footer);
    var report = new Report(() -> Stream.of(section));
    var lines = report.render();
    assertEquals("", lines.get(lines.size() - 1).trim());
  }
}
