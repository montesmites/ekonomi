package se.montesmites.ekonomi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CurrencyTest {

  @Test
  void zero() {
    var exp = Currency.of(0);
    var act = Currency.zero();
    assertEquals(exp, act);
  }

  @Test
  void of() {
    var exp = 100;
    var act = Currency.of(100);
    assertEquals(exp, act.getAmount());
  }

  @Test
  void add() {
    var exp = Currency.of(100 + 200);
    var act = Currency.of(100).add(Currency.of(200));
    assertEquals(exp, act);
  }

  @Test
  void negate() {
    var exp = Currency.of(-100);
    var act = Currency.of(100).negate();
    assertEquals(exp, act);
  }
}
