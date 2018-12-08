package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertBodyRowDescriptions;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertExpectedAverages;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.assertMonthlyAmounts;
import static se.montesmites.ekonomi.report.CashflowReport_AccountGroup_2012.bodyRowsOf;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class CashflowReport_TwoSections_OneRowEach_Test {

  @OrganizationInjector private Organization organization;
  private CashflowDataFetcher fetcher;
  private CashflowReport report;

  @BeforeEach
  void before() {
    this.fetcher = new CashflowDataFetcher(this.organization);
    this.report = new CashflowReport(() -> sections().stream().map(Map.Entry::getKey));
  }

  private List<Map.Entry<Section, List<CashflowReport_AccountGroup_2012>>> sections() {
    return List.of(
        section("Bokfört resultat", List.of(BOKFORT_RESULTAT)),
        section("Kortfristiga skulder", List.of(KORTFRISTIGA_SKULDER)));
  }

  private Map.Entry<Section, List<CashflowReport_AccountGroup_2012>> section(
      String title, List<CashflowReport_AccountGroup_2012> groups) {
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(() -> bodyRowsOf(fetcher, groups));
    var footer = Footer.of(body.aggregate());
    var section = Section.of(header, body, footer);
    return Map.entry(section, groups);
  }

  @Test
  void sectionCount() {
    assertEquals(sections().size(), report.streamSections().count());
  }

  @Test
  void body_rowDescription() {
    sections().forEach(section -> assertBodyRowDescriptions(section.getKey(), section.getValue()));
  }

  @Test
  void testMonthlyAmountsForGroupedRow() {
    sections().forEach(section -> assertMonthlyAmounts(section.getKey(), section.getValue()));
  }

  @Test
  void testAverage() {
    sections().forEach(section -> assertExpectedAverages(section.getKey(), section.getValue()));
  }
}
