package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.Aggregate;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Row;

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
  void add() {
    var footerBuilder = new FooterBuilder(Body::empty);
    var row = Row.title("title");
    var exp = Footer.of(row);
    var act = footerBuilder.add(row).footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }

  @Test
  void aggregateBody() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var accountGroups = List.of(AccountGroup.of("1111", "1111"), AccountGroup.of("2222", "2222"));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var bodyBuilder = new BodyBuilder(year, amountsFetcher).accountGroups(accountGroups);
    var footerBuilder = new FooterBuilder(bodyBuilder::body).aggregateBody();
    var exp = Footer.of(Aggregate.of(List.of(row1, row2)).asRow());
    var act = footerBuilder.footer();
    assertEquals(exp.asString("\n"), act.asString("\n"));
  }
}
