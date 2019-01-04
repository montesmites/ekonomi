package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.time.Year;
import java.util.EnumSet;
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
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.Tag;
import se.montesmites.ekonomi.report.TagFilter;

class ReportBuilder_SubtotalTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void subtotal_materializedBody() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotal_dematerializedBody() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(
                        body ->
                            body.accountGroups(List.of(AccountGroup.of("", "2222")))
                                .dematerialize()))
            .subtotal(description);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotal_twoSubtotals() {
    var description1 = "description1";
    var description2 = "description2";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var row3 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var subtotal1 =
        AmountsProvider.of(description1, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var subtotal2 =
        AmountsProvider.of(description2, month -> Optional.of(Currency.of(month.ordinal() * 600)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2),
                entry(new AccountId(yearId, "3333"), row3)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description1)
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "3333")))))
            .subtotal(description2);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())),
            Section.of(Header.empty(), Body.of(row3), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotal_withTwoUntouchedMonths() {
    var touchedMonths =
        EnumSet.of(
            Month.JANUARY,
            Month.FEBRUARY,
            Month.MARCH,
            Month.APRIL,
            Month.MAY,
            Month.JUNE,
            Month.JULY,
            Month.AUGUST,
            Month.SEPTEMBER,
            Month.OCTOBER);
    var description1 = "description1";
    var description2 = "description2";
    var row1 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 100))
                    : Optional.empty());
    var row2 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 200))
                    : Optional.empty());
    var row3 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 300))
                    : Optional.empty());
    var subtotal1 =
        AmountsProvider.of(
            description1,
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 300))
                    : Optional.empty());
    var subtotal2 =
        AmountsProvider.of(
            description2,
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 600))
                    : Optional.empty());
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2),
                entry(new AccountId(yearId, "3333"), row3)))
            .touchedMonths(Map.of(year, touchedMonths))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description1)
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "3333")))))
            .subtotal(description2);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())),
            Section.of(Header.empty(), Body.of(row3), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotal_noSections() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal = AmountsProvider.of(description, __ -> Optional.of(Currency.zero()));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder = new ReportBuilder(amountsFetcher, year).subtotal(description);
    var exp =
        List.of(Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotalForAnyTag_twoSectionsWithSameTag() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var tag1 = Tag.of("tag1");
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111"))))
                        .tag(tag1))
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222"))))
                        .tag(tag1))
            .subtotal(description);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotalForAnyTag_twoSectionsWithDifferentTags() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var tag1 = Tag.of("tag1");
    var tag2 = Tag.of("tag2");
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111"))))
                        .tag(tag1))
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222"))))
                        .tag(tag2))
            .subtotal(description);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotalForOneTag_twoSectionsWithDifferentTags() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var tag1 = Tag.of("tag1");
    var tag2 = Tag.of("tag2");
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111"))))
                        .tag(tag1))
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222"))))
                        .tag(tag2))
            .subtotal(description, TagFilter.isEqualTo(tag1));
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void subtotalOnceForEachTagAndOnceForBoth_twoSectionsWithDifferentTags() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal1 =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var subtotal2 =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal3 =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var tag1 = Tag.of("tag1");
    var tag2 = Tag.of("tag2");
    var tag3 = Tag.of("tag3");
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111"))))
                        .tag(tag1)
                        .tag(tag3))
            .subtotal(description, TagFilter.isEqualTo(tag1))
            .section(
                section ->
                    section
                        .body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222"))))
                        .tag(tag2)
                        .tag(tag3))
            .subtotal(description, TagFilter.isEqualTo(tag2))
            .subtotal(description, TagFilter.isEqualTo(tag3));
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal3.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }
}
