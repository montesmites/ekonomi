package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

class BodyBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void empty() {
    var bodyBuilder = BodyBuilder.empty();
    var exp = Body.empty();
    var act = bodyBuilder.body();
    assertTrue(bodyBuilder.isMaterialized());
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void fromAccountGroups() {
    var description1 = "description1";
    var description2 = "description2";
    var accountGroups =
        List.of(AccountGroup.of(description1, "1111"), AccountGroup.of(description2, "2222"));
    var row1 =
        AmountsProvider.of(description1, month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(description2, month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var bodyBuilder = new BodyBuilder(year, amountsFetcher);
    var exp = Body.of(List.of(row1, row2));
    var act = bodyBuilder.accountGroups(accountGroups).body();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void isMaterialized_negated() {
    var bodyBuilder = BodyBuilder.empty();
    var exp = Body.empty();
    var act = bodyBuilder.dematerialize().body();
    assertAll(
        () -> assertEquals(exp.asString("\n"), act.asString("\n")),
        () -> assertFalse(bodyBuilder.isMaterialized()));
  }
}
