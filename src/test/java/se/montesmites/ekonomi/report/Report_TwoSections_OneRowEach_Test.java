package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.KORTFRISTIGA_SKULDER;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertBodyRowDescriptions;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertExpectedAverages;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertExpectedTotals;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertMonthlyAmounts;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.bodyRowsOf;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class Report_TwoSections_OneRowEach_Test {

  @OrganizationInjector private Organization organization;
  private DataFetcher fetcher;
  private Report report;

  @BeforeEach
  void before() {
    this.fetcher = new DataFetcher(this.organization);
    this.report = new Report(() -> sections().stream().map(Map.Entry::getKey));
  }

  private List<Map.Entry<Section, List<Report_AccountGroup_2012>>> sections() {
    return List.of(
        section("Bokfört resultat", List.of(BOKFORT_RESULTAT)),
        section("Kortfristiga skulder", List.of(KORTFRISTIGA_SKULDER)));
  }

  private Map.Entry<Section, List<Report_AccountGroup_2012>> section(
      String title, List<Report_AccountGroup_2012> groups) {
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(() -> bodyRowsOf(fetcher, groups));
    var footer = Footer.of(Aggregate.of(body).asRow());
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

  @Test
  void testTotal() {
    sections().forEach(section -> assertExpectedTotals(section.getKey(), section.getValue()));
  }
}
