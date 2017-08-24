package se.montesmites.ekonomi.report;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.organization.Filter;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

import java.nio.file.Path;
import java.time.Month;
import java.time.Year;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static junit.framework.TestCase.assertEquals;

public class TouchedYearMonthTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private OrganizationBuilder organizationBuilder;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Test
    public void oneMonth() throws Exception {
        final Path path = tempfolder.getRoot().toPath();
        this.organizationBuilder = new OrganizationBuilder(path, Filter.get(filterEntry(EnumSet.of(JANUARY))));
        CashflowDataFetcher fetcher = new CashflowDataFetcher(organizationBuilder.build());
        Set<Month> exp = EnumSet.of(JANUARY);
        Set<Month> act = fetcher.touchedMonths(year);
        assertEquals(exp, act);
    }

    @Test
    public void twoMonths() throws Exception {
        final Path path = tempfolder.getRoot().toPath();
        this.organizationBuilder = new OrganizationBuilder(path, Filter.get(filterEntry(EnumSet.of(JANUARY, FEBRUARY))));
        CashflowDataFetcher fetcher = new CashflowDataFetcher(organizationBuilder.build());
        Set<Month> exp = EnumSet.of(JANUARY, FEBRUARY);
        Set<Month> act = fetcher.touchedMonths(year);
        assertEquals(exp, act);
    }

    private Predicate<Entry> filterEntry(Set<Month> months) {
        return entry
                -> organizationBuilder.getEventManager().getEvent(entry.getEventId())
                .map(Event::getDate)
                .filter(date -> date.getYear() == year.getValue())
                .filter(date -> months.contains(date.getMonth()))
                .isPresent();
    }
}
