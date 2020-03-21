package se.montesmites.ekonomi.report;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.Month;
import java.time.Year;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.organization.Filter;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import testdata.DefaultTestDataExtension;
import testdata.PathToBinaryFiles;

@ExtendWith(DefaultTestDataExtension.class)
class TouchedYearMonthTest {

  private final Year year = Year.of(2012);

  @PathToBinaryFiles private Path pathToBinaryFiles;
  private OrganizationBuilder organizationBuilder;

  @Test
  void oneMonth() {
    this.organizationBuilder =
        new OrganizationBuilder(pathToBinaryFiles, Filter.get(filterEntry(EnumSet.of(JANUARY))));
    DataFetcher fetcher = new DataFetcher(organizationBuilder.build());
    Set<Month> exp = EnumSet.of(JANUARY);
    Set<Month> act = fetcher.touchedMonths(year);
    assertEquals(exp, act);
  }

  @Test
  void twoMonths() {
    this.organizationBuilder =
        new OrganizationBuilder(
            pathToBinaryFiles, Filter.get(filterEntry(EnumSet.of(JANUARY, FEBRUARY))));
    DataFetcher fetcher = new DataFetcher(organizationBuilder.build());
    Set<Month> exp = EnumSet.of(JANUARY, FEBRUARY);
    Set<Month> act = fetcher.touchedMonths(year);
    assertEquals(exp, act);
  }

  private Predicate<Entry> filterEntry(Set<Month> months) {
    return entry ->
        organizationBuilder
            .getEventManager()
            .getEvent(entry.eventId())
            .map(Event::date)
            .filter(date -> date.getYear() == year.getValue())
            .filter(date -> months.contains(date.getMonth()))
            .isPresent();
  }
}
