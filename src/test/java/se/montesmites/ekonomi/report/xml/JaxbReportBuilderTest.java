package se.montesmites.ekonomi.report.xml;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.stream.XMLInputFactory;
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
  private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

  private final Year year = Year.now();
  private final YearId yearId = new YearId(String.valueOf(year.getValue()));

  @Test
  void accountGroupsElement() throws Exception {
    var path = PATH_TO_TEST_XML + "01_account-groups.xml";
    var amounts1 = AmountsProvider
        .of("1111", month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amounts2 = AmountsProvider
        .of("2222", month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amounts3 = AmountsProvider.of("", month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), amounts1),
                entry(new AccountId(yearId, "2222"), amounts2)))
            .amountsFetcher();
    var builder = new JaxbReportBuilder(Paths.get(getClass().getResource(path).toURI()));
    var report = builder.report(amountsFetcher, year);
    var exp =
        Section.of(
            Header.of(
                List.of(Row.title("1111 & 2222"), Row.descriptionWithMonths("", Row.SHORT_MONTHS))),
            Body.of(List.of(amounts1, amounts2)),
            Footer.of(amounts3.asRow()));
    var act = report.streamSections().collect(toList());
    assertAll(
        () -> assertEquals(1, act.size()),
        () -> assertEquals(exp.asString(), act.get(0).asString()));
  }
}
