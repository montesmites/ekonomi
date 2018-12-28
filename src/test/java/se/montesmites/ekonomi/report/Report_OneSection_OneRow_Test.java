package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.BOKFORT_RESULTAT;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertBodyRowDescriptions;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.assertMonthlyAmounts;
import static se.montesmites.ekonomi.report.Report_AccountGroup_2012.bodyRowsOf;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.organization.Organization;
import testdata.DefaultTestDataExtension;
import testdata.OrganizationInjector;

@ExtendWith(DefaultTestDataExtension.class)
class Report_OneSection_OneRow_Test {

  private static final String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";

  @OrganizationInjector private Organization organization;
  private DataFetcher fetcher;
  private Report report;
  private Section section;
  private List<Report_AccountGroup_2012> groups;

  @BeforeEach
  void before() {
    this.fetcher = new DataFetcher(this.organization);
    this.report = new Report(this::sections);
    this.groups = List.of(BOKFORT_RESULTAT);
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
