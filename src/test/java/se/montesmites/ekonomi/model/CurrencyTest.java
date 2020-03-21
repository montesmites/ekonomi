package se.montesmites.ekonomi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CurrencyTest {

  @Test
  void zero() {
    var exp = new Currency(0);
    var act = Currency.zero();
    assertEquals(exp, act);
  }

  @Test
  void of() {
    var exp = 100;
    var act = new Currency(100);
    assertEquals(exp, act.amount());
  }

  @Test
  void add() {
    var exp = new Currency(100 + 200);
    var act = new Currency(100).add(new Currency(200));
    assertEquals(exp, act);
  }

  @Test
  void negate() {
    var exp = new Currency(-100);
    var act = new Currency(100).negate();
    assertEquals(exp, act);
  }
}
