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

  @Test
  void header() {
    var amountFetcher = AmountFetcher.empty();
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    var exp = Header.of(Row.title("title")).asString("\n");
    var act = sectionBuilder.header(header -> header.title("title")).getHeader().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void body() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    var exp = Body.of(List.of(row1, row2)).asString("\n");
    var act =
        sectionBuilder
            .body(
                body ->
                    body.accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222"))))
            .getBody()
            .asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void footer() {
    var body1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var body2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), body1),
                entry(new AccountId(yearId, "2222"), body2)))
            .amountFetcher();
    var sectionBuilder =
        new SectionBuilder(year, amountFetcher)
            .body(
                body ->
                    body.accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222")))
                        .dematerialize());
    var exp = sectionBuilder.getBody().aggregate("").asRow().asString();
    var act = sectionBuilder.footer(FooterBuilder::aggregateBody).getFooter().asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void section_nonTransientBody() {
    var title = Row.title("title");
    var body1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var body2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var expBody = Body.of(List.of(body1, body2));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), body1),
                entry(new AccountId(yearId, "2222"), body2)))
            .amountFetcher();
    var footer = Footer.of(List.of(expBody.aggregate("").asRow()));
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    var exp = Section.of(Header.of(title), expBody, footer).asString("\n");
    var act =
        sectionBuilder
            .header(header -> header.title("title"))
            .body(
                body ->
                    body.accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222"))))
            .footer(FooterBuilder::aggregateBody)
            .section()
            .asString("\n");
    assertEquals(exp, act);
  }

  @Test
  void section_transientBody() {
    var title = Row.title("title");
    var body1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var body2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var expBody = Body.of(List.of(body1, body2));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), body1),
                entry(new AccountId(yearId, "2222"), body2)))
            .amountFetcher();
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    var footer = Footer.of(List.of(expBody.aggregate("").asRow()));
    var exp = Section.of(Header.of(title), Body.empty(), footer).asString("\n");
    var act =
        sectionBuilder
            .header(header -> header.title("title"))
            .body(
                body ->
                    body.accountGroups(
                        List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222")))
                        .dematerialize())
            .footer(FooterBuilder::aggregateBody)
            .section()
            .asString("\n");
    assertEquals(exp, act);
  }
}
