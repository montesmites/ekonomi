package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertBodyRowDescriptions;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertMonthlyAmounts;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.bodyRowsOf;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class CashflowReport_OneSection_TwoRows_Test {

  private static final String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";

  @OrganizationInjector private Organization organization;
  private CashflowDataFetcher fetcher;
  private CashflowReport report;
  private Section section;
  private List<CashflowReport_AccountGroup_2012> groups;

  @BeforeEach
  void before() {
    this.fetcher = new CashflowDataFetcher(this.organization);
    this.report = new CashflowReport(this::sections);
    this.groups = List.of(BOKFORT_RESULTAT, KORTFRISTIGA_SKULDER);
    var header =
        Header.of(Row.title(DEN_LOPANDE_VERKSAMHETEN))
            .add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(() -> bodyRowsOf(fetcher, groups));
    var footer = Footer.of(body.aggregate("").asRow());
    this.section = Section.of(header, body, footer);
  }

  private Stream<Section> sections() {
    return Stream.of(section);
  }

  @Test
  void exactlyOneSection() {
    assertEquals(1, report.streamSections().count());
  }

  @Test
  void sectionTitle() {
    var exp = DEN_LOPANDE_VERKSAMHETEN.toUpperCase();
    var act = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
    assertEquals(exp, act);
  }

  @Test
  void body_rowDescription() {
    assertBodyRowDescriptions(section, groups);
  }

  @Test
  void testMonthlyAmountsForGroupedRow() {
    assertMonthlyAmounts(section, groups);
  }
}
