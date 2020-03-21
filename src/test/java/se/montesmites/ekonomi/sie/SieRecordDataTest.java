package se.montesmites.ekonomi.sie;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.sie.record.SieRecordData;
import se.montesmites.ekonomi.sie.record.SieToken;

class SieRecordDataTest {

  @Test
  void testWhiteSpace() {
    var data = SieRecordData.of("A B\tC  D\t\tE \tF");

    var expTokens = Stream.of("A", "B", "C", "D", "E", "F").map(SieToken::of).collect(toList());
    var actTokens = data.getTokens();

    assertEquals(expTokens, actTokens);
  }

  @Test
  void testDoubleQuotes() {
    var data = SieRecordData.of("First \"and then second\"");

    var expTokens = Stream.of("First", "and then second").map(SieToken::of).collect(toList());
    var actTokens = data.getTokens();

    assertEquals(expTokens, actTokens);
  }

  @Test
  void testEscapedDoubleQuote() {
    var first = "\"Some \\\"quoted\\\" quotes\"";
    var second = "\"Some more \\\"quoted\\\" quotes\"";
    var data = SieRecordData.of(first + " " + second);

    var expTokens =
        Stream.of("Some \"quoted\" quotes", "Some more \"quoted\" quotes")
            .map(SieToken::of)
            .collect(toList());
    var actTokens = data.getTokens();

    assertEquals(expTokens, actTokens);
  }

  @Test
  void testEmptyFields() {
    var first = "\"\"";
    var second = "second";
    var third = "\"\"";
    var data = SieRecordData.of(first + " " + second + " " + third);

    var expTokens = Stream.of("", "second", "").map(SieToken::of).collect(toList());
    var actTokens = data.getTokens();

    assertEquals(expTokens, actTokens);
  }

  @Test
  void testAsInt() {
    var data = SieRecordData.of("123");
    var exp = List.of(123);
    var act = data.getTokens().stream().map(SieToken::asInt).collect(toList());
    assertEquals(exp, act);
  }

  @ParameterizedTest
  @CsvSource({
      "123,12300",
      "123.45,12345",
      "-412.50,-41250",
      "-0.50,-50",
      "-0.01, -1",
      "-44199.85, -4419985"
  })
  void testAsCurrency(String input, long expected) {
    var data = SieRecordData.of(input);
    var exp = List.of(new Currency(expected));
    var act = data.getTokens().stream().map(SieToken::asCurrency).collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void testAsDate() {
    var data = SieRecordData.of("20190101");
    var exp = List.of(LocalDate.parse("2019-01-01"));
    var act = data.getTokens().stream().map(SieToken::asDate).collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void testAbsentTokens() {
    var data = SieRecordData.of("A B C");
    assertAll(
        () -> assertEquals("A", data.get(0).asString()),
        () -> assertEquals("B", data.get(1).asString()),
        () -> assertEquals("C", data.get(2).asString()),
        () -> assertEquals("", data.get(3).asString()),
        () -> assertEquals("", data.get(100).asString()));
  }
}
