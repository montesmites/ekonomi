package se.montesmites.ekonomi.organization;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class OrganizationTest {

    @Rule
    public TemporaryFolder tempfolder = new TemporaryFolder();

    private Organization organization;
    private ResourceToFileCopier copier;

    @Before
    public void before() throws Exception {
        this.copier = new ResourceToFileCopier();
        setupOrganization();
    }

    @Test
    public void readEvent_2012_A1() throws Exception {
        EventId eventId = new EventId(new YearId("C"), 1, new Series("A"));
        Event event = organization.getEvent(eventId).get();
        assertEquals(eventId, event.getEventId());
        assertEquals(LocalDate.parse("2012-01-12"), event.getDate());
        assertEquals("Överföring till sparkonto", event.getDescription());
        assertEquals(LocalDate.parse("2011-01-31"), event.getRegistrationDate());
    }

    private void setupOrganization() {
        List<BinaryFile_2015_0> files = Arrays.asList(BinaryFile_2015_0.EVENTS);
        files.stream().forEach(f -> copier.copyTestFile(f, tempfolder));
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
    }
}
