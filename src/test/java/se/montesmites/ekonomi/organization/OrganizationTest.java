package se.montesmites.ekonomi.organization;

import java.time.LocalDate;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class OrganizationTest {
    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private Organization organization;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }
    
    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
    }
    
    @Test
    public void readYearByYear() throws Exception {
        Year year = organization.getYear(java.time.Year.of(2012)).get();
        YearId yearId = new YearId("C");
        assertEquals(yearId, year.getYearId());
        assertEquals(LocalDate.parse("2012-01-01"), year.getFrom());
        assertEquals(LocalDate.parse("2012-12-31"), year.getTo());
    }

    @Test
    public void readEvent_2012_A1() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        EventId eventId = new EventId(yearId, 1, new Series("A"));
        Event event = organization.getEvent(eventId).get();
        assertEquals(eventId, event.getEventId());
        assertEquals(LocalDate.parse("2012-01-12"), event.getDate());
        assertEquals("Överföring till sparkonto", event.getDescription());
        assertEquals(LocalDate.parse("2011-01-31"), event.getRegistrationDate());
    }
}
