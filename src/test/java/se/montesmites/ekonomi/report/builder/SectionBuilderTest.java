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
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

class SectionBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());
  private AmountFetcher amountFetcher = AmountFetcher.empty();

  @Test
  void header() {
    var sectionBuilder = new SectionBuilder();
    var header = Header.of(Row.title("title"));
    var exp = header.asString("\n");
    var act = sectionBuilder.header(new HeaderBuilder().title("title")).getHeader().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void body() {
    var sectionBuilder = new SectionBuilder();
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    this.amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var body = Body.of(List.of(row1, row2));
    var exp = body.asString("\n");
    var act =
        sectionBuilder
            .body(
                bodyBuilder()
                    .accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222"))))
            .getBody()
            .asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void footer() {
    var sectionBuilder = new SectionBuilder();
    var row = Row.title("title");
    var footer = Footer.of(row);
    var exp = footer.asString("\n");
    var act = sectionBuilder.footer(footer).getFooter().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void section() {
    var title = Row.title("title");
    var body1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var body2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var sectionBuilder = new SectionBuilder();
    var header = Header.of(title);
    var body = Body.of(List.of(body1, body2));
    this.amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), body1),
                entry(new AccountId(yearId, "2222"), body2)))
            .amountFetcher();
    var footer = Footer.of(title);
    var exp = Section.of(header, body, footer).asString("\n");
    var act =
        sectionBuilder
            .header(new HeaderBuilder().title("title"))
            .body(
                bodyBuilder()
                    .accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222"))))
            .footer(footer)
            .section()
            .asString("\n");
    assertEquals(exp, act);
  }

  private BodyBuilder bodyBuilder() {
    return new BodyBuilder(year, amountFetcher);
  }
}
