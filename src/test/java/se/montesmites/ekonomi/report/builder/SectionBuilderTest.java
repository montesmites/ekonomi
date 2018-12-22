package se.montesmites.ekonomi.report.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
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

class SectionBuilderTest implements AmountFetcher {

  private final YearId yearId = new YearId("A");
  private final Year year = Year.of(2012);

  private BiFunction<AccountId, YearMonth, Optional<Currency>> fetchAmount =
      (__, ___) -> Optional.empty();
  private Function<AccountId, Optional<Balance>> fetchBalance = __ -> Optional.empty();
  private BiFunction<Year, Predicate<AccountId>, Stream<AccountId>> streamAccountIds =
      (__, ___) -> Stream.empty();
  private Function<Year, Set<Month>> touchedMonths = __ -> Set.of();

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
    setUpAmountFetcher(row1, row2);
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
    setUpAmountFetcher(body1, body2);
    var footer = Footer.of(title);
    var exp = Section.of(header, body, footer).asString("\n");
    var act =
        sectionBuilder
            .header(new HeaderBuilder().title("title"))
            .body(bodyBuilder().accountGroups(
                List.of(AccountGroup.of("", "1111"), AccountGroup.of("", "2222"))))
            .footer(footer)
            .section()
            .asString("\n");
    assertEquals(exp, act);
  }

  private BodyBuilder bodyBuilder() {
    return new BodyBuilder(year, this);
  }

  @Override
  public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
    return this.fetchAmount.apply(accountId, yearMonth);
  }

  @Override
  public Optional<Balance> fetchBalance(AccountId accountId) {
    return fetchBalance.apply(accountId);
  }

  @Override
  public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
    return streamAccountIds.apply(year, filter);
  }

  @Override
  public Set<Month> touchedMonths(Year year) {
    return touchedMonths.apply(year);
  }

  private void setUpAmountFetcher(AmountsProvider row1, AmountsProvider row2) {
    this.streamAccountIds =
        (__, filter) ->
            Stream.of(new AccountId(yearId, "1111"), new AccountId(yearId, "2222")).filter(filter);
    this.fetchAmount =
        (accountId, yearMonth) ->
            (accountId.getId().equals("1111") ? row1 : row2)
                .getMonthlyAmount(yearMonth.getMonth())
                .map(Currency::negate);
    this.touchedMonths = __ -> EnumSet.allOf(Month.class);
  }
}
