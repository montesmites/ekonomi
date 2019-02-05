package se.montesmites.ekonomi.report.xml;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.APRIL;
import static se.montesmites.ekonomi.report.Column.AUGUST;
import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DECEMBER;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.FEBRUARY;
import static se.montesmites.ekonomi.report.Column.JANUARY;
import static se.montesmites.ekonomi.report.Column.JULY;
import static se.montesmites.ekonomi.report.Column.JUNE;
import static se.montesmites.ekonomi.report.Column.MARCH;
import static se.montesmites.ekonomi.report.Column.MAY;
import static se.montesmites.ekonomi.report.Column.NOVEMBER;
import static se.montesmites.ekonomi.report.Column.OCTOBER;
import static se.montesmites.ekonomi.report.Column.SEPTEMBER;
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.builder.AmountsFetcherBuilder;

class JaxbReportBuilderTest {

  private static final String PATH_TO_TEST_XML = "/se/montesmites/ekonomi/report/xml/";

  private final Year year = Year.now();
  private final YearId yearId = new YearId(String.valueOf(year.getValue()));

  @Test
  void accountGroupsConstituent() throws Exception {
    var path = PATH_TO_TEST_XML + "01_account-groups.xml";
    var amounts1 =
        AmountsProvider.of("1111", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amounts2 =
        AmountsProvider.of("2222", month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var sum = AmountsProvider.of("", month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), amounts1),
                entry(new AccountId(yearId, "2222"), amounts2)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp =
        List.of(
            Section.of(
                Header.of(
                    List.of(
                        Row.title("1111 & 2222"), Row.descriptionWithMonths("", Row.SHORT_MONTHS))),
                Body.of(List.of(amounts1, amounts2)),
                Footer.of(sum.asRow())));
    var act = report.streamSections().collect(toList());
    assertEquals(asString(exp), asString(act));
  }

  @Test
  void subtotalConstituent() throws Exception {
    var path = PATH_TO_TEST_XML + "02_subtotal.xml";
    var amounts1 =
        AmountsProvider.of("1111", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amounts2 =
        AmountsProvider.of("2222", month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amounts3 =
        AmountsProvider.of("3333", month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var sum = AmountsProvider.of("", month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var subtotal =
        AmountsProvider.of("SUBTOTAL", month -> Optional.of(Currency.of(month.ordinal() * 600)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), amounts1),
                entry(new AccountId(yearId, "2222"), amounts2),
                entry(new AccountId(yearId, "3333"), amounts3)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp =
        List.of(
            Section.of(
                Header.of(
                    List.of(
                        Row.title("1111 & 2222"), Row.descriptionWithMonths("", Row.SHORT_MONTHS))),
                Body.of(List.of(amounts1, amounts2)),
                Footer.of(sum.asRow())),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())));
    var act = report.streamSections().collect(toList());
    assertEquals(asString(exp), asString(act));
  }

  @Test
  void sectionConstituent() throws Exception {
    var path = PATH_TO_TEST_XML + "03_section.xml";
    var subtotal =
        AmountsProvider.of("SUBTOTAL", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), subtotal)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp = List.of(Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())));
    var act = report.streamSections().collect(toList());
    assertEquals("\n" + asString(exp), asString(act));
  }

  @Test
  void negateAccountGroup() throws Exception {
    var path = PATH_TO_TEST_XML + "04_negate.xml";
    var amounts1 =
        AmountsProvider.of("1111", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var sum = AmountsProvider.of("", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), amounts1)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp =
        List.of(
            Section.of(
                Header.of(
                    List.of(
                        Row.title("negate 1111"), Row.descriptionWithMonths("", Row.SHORT_MONTHS))),
                Body.of(List.of(amounts1.negate())),
                Footer.of(sum.negate().asRow())));
    var act = report.streamSections().collect(toList());
    assertEquals(asString(exp), asString(act));
  }

  @Test
  void accumulateAccountGroups() throws Exception {
    var path = PATH_TO_TEST_XML + "05_accumulate-account-groups.xml";
    var amounts1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.getValue() * 100)));
    var accumulation =
        (Row)
            column ->
                Map.ofEntries(
                    entry(DESCRIPTION, Currency.of(0).format()),
                    entry(JANUARY, Currency.of(100).format()),
                    entry(FEBRUARY, Currency.of(300).format()),
                    entry(MARCH, Currency.of(600).format()),
                    entry(APRIL, Currency.of(1000).format()),
                    entry(MAY, Currency.of(1500).format()),
                    entry(JUNE, Currency.of(2100).format()),
                    entry(JULY, Currency.of(2800).format()),
                    entry(AUGUST, Currency.of(3600).format()),
                    entry(SEPTEMBER, Currency.of(4500).format()),
                    entry(OCTOBER, Currency.of(5500).format()),
                    entry(NOVEMBER, Currency.of(6600).format()),
                    entry(DECEMBER, Currency.of(7800).format()),
                    entry(TOTAL, Currency.of(0).format()),
                    entry(AVERAGE, Currency.of(3033).format()))
                    .get(column);
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), amounts1)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp =
        List.of(
            Section.of(
                Header.of(Row.title("accumulate"))
                    .add(Row.descriptionWithMonths("", Row.SHORT_MONTHS)),
                Body.empty(),
                Footer.of(accumulation)));
    var act = report.streamSections().collect(toList());
    assertEquals(asString(exp), asString(act));
  }

  private String asString(List<Section> sections) {
    return sections.stream().map(row -> row.asString("")).collect(joining("\n"));
  }
}
