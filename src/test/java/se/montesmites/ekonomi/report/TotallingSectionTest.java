package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.bodyRowsOf;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class TotallingSectionTest {

  private static final String TOTALLING_SECTION_TITLE = "CHECKSUM";

  @OrganizationInjector private Organization organization;
  private CashflowDataFetcher fetcher;
  private Section section1;
  private Section section2;
  private TotallingSection totallingSection;

  @BeforeEach
  void before() {
    this.fetcher = new CashflowDataFetcher(this.organization);
    section1 =
        Section.of(
            Header.of(() -> "Section 1").add(SHORT_MONTHS_HEADER),
            () -> bodyRowsOf(fetcher, List.of(BOKFORT_RESULTAT)));
    section2 =
        Section.of(
            Header.of(() -> "Section 2").add(SHORT_MONTHS_HEADER),
            () -> bodyRowsOf(fetcher, List.of(KORTFRISTIGA_SKULDER)));
    totallingSection = new TotallingSection(TOTALLING_SECTION_TITLE, List.of(section1, section2));
  }

  @Test
  void assertTitle() {
    Assertions.assertEquals(
        TOTALLING_SECTION_TITLE,
        totallingSection.header().stream().findFirst().orElseThrow().format(DESCRIPTION));
  }

  @Test
  void assertNoBodyRows() {
    Assertions.assertEquals(0, totallingSection.body().stream().count());
  }

  @Test
  void assertTotals() {
    Column.streamMonths()
        .forEach(
            month ->
                Assertions.assertEquals(
                    expectedMonthlyTotal(month),
                    totallingSection
                        .footer()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .asRowWithAmounts()
                        .orElseThrow()
                        .getMonthlyAmount(month),
                    month.name()));
  }

  private Currency expectedMonthlyTotal(Column month) {
    return Stream.of(section1, section2)
        .map(
            section ->
                section
                    .footer()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .asRowWithAmounts()
                    .orElseThrow()
                    .getMonthlyAmount(month))
        .reduce(new Currency(0), Currency::add);
  }
}
