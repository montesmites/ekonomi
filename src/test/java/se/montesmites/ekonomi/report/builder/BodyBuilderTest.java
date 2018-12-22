package se.montesmites.ekonomi.report.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.report.Body;

class BodyBuilderTest {

  @Test
  void empty() {
    var bodyBuilder = new BodyBuilder();
    var exp = Body.empty();
    var act = bodyBuilder.body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
