package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;

class FooterBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void empty() {
    var footerBuilder = FooterBuilder.empty();
    var exp = Footer.empty();
    var act = footerBuilder.footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void aggregateBody() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var accountGroups = List.of(AccountGroup.of("1111", "1111"), AccountGroup.of("2222", "2222"));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var bodyBuilder = new BodyBuilder(year, amountFetcher).accountGroups(accountGroups);
    var footerBuilder = new FooterBuilder(bodyBuilder::body).aggregateBody();
    var exp = Footer.of(Body.of(List.of(row1, row2)).aggregate("").asRow());
    var act = footerBuilder.footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void accumulate() {
    var row = AmountsProvider.of(month -> Optional.of(Currency.of((month.ordinal() + 1) * 100)));
    var initialBalance = Currency.of(100);
    var accountId = new AccountId(yearId, "1111");
    var accountGroups = List.of(AccountGroup.of("1111", "1111"));
    var amountFetcher =
        AmountFetcherBuilder.of(Map.ofEntries(entry(accountId, row)))
            .balances(Map.of(accountId, Optional.of(new Balance(accountId, initialBalance))))
            .amountFetcher();
    var bodyBuilder =
        new BodyBuilder(year, amountFetcher).accountGroups(accountGroups).isTransient();
    var footerBuilder = new FooterBuilder(bodyBuilder::body).accumulateBody(initialBalance);
    var exp = Footer.of(row.accumulate(initialBalance).asRow());
    var act = footerBuilder.footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
