package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class AccountGroupTest {

  private static final String DESCRIPTION = "description";
  private static final String REGEX = "regex";
  private static final AmountsProvider AMOUNTS_PROVIDER =
      column -> Optional.of(Currency.of(column.ordinal() * 100));

  @Test
  void of() {
    var group = AccountGroup.of(DESCRIPTION, REGEX);
    assertAll(
        () -> assertEquals(DESCRIPTION, group.description()),
        () -> assertEquals(REGEX, group.regex()));
  }

  @Test
  void defaultPostProcessor() {
    var group = AccountGroup.of(DESCRIPTION, REGEX);
    assertTrue(group.postProcessor().apply(AMOUNTS_PROVIDER).isEquivalentTo(AMOUNTS_PROVIDER));
  }

  @Test
  void postProcessor() {
    var group = AccountGroup.of(DESCRIPTION, REGEX).postProcessor(AmountsProvider::negate);
    assertAll(
        () -> assertEquals(DESCRIPTION, group.description()),
        () -> assertEquals(REGEX, group.regex()),
        () -> assertTrue(group.postProcessor().apply(AMOUNTS_PROVIDER).isEquivalentTo(
            AMOUNTS_PROVIDER.negate())));
  }
}
