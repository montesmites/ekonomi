package se.montesmites.ekonomi.report.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.report.Footer;

class FooterBuilderTest {

  @Test
  void empty() {
    var footerBuilder = FooterBuilder.empty();
    var exp = Footer.empty();
    var act = footerBuilder.footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
