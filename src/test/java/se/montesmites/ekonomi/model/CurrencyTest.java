package se.montesmites.ekonomi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CurrencyTest {

  @Test
  void of() {
    var exp = new Currency(100);
    var act = Currency.of(100);
    assertEquals(exp, act);
  }

  @Test
  void add() {
    var exp = new Currency(100 + 200);
    var act = Currency.of(100).add(Currency.of(200));
    assertEquals(exp, act);
  }

  @Test
  void negate() {
    var exp = new Currency(-100);
    var act = Currency.of(100).negate();
    assertEquals(exp, act);
  }
}
