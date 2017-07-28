package se.montesmites.ekonomi.organization;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.EntryEvent;
import se.montesmites.ekonomi.model.EntryStatus;

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
    public void readYear_byYear_2012() throws Exception {
        Year year = organization.getYear(java.time.Year.of(2012)).get();
        YearId yearId = new YearId("C");
        assertEquals(yearId, year.getYearId());
        assertEquals(LocalDate.parse("2012-01-01"), year.getFrom());
        assertEquals(LocalDate.parse("2012-12-31"), year.getTo());
    }

    @Test
    public void readEvent_byEventId_2012A1() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        EventId eventId = new EventId(yearId, 1, new Series("A"));
        Event event = organization.getEvent(eventId).get();
        assertEquals(eventId, event.getEventId());
        assertEquals(LocalDate.parse("2012-01-12"), event.getDate());
        assertEquals("Överföring till sparkonto", event.getDescription());
        assertEquals(LocalDate.parse("2011-01-31"), event.getRegistrationDate());
    }

    @Test
    public void readEntries_byEventId_2012A1() throws Exception {
        YearId yearId = organization.getYear(java.time.Year.of(2012)).get().getYearId();
        EventId eventId = new EventId(yearId, 1, new Series("A"));
        List<Entry> actEntries = organization.getEntries(eventId).get().stream()
                .sorted(comparing(entry -> entry.getAccountId().getId()))
                .collect(toList());
        List<Entry> expEntries = Arrays.asList(
                entry(eventId, "1920", -50000000),
                entry(eventId, "1940", 50000000));
        assertEquals(expEntries, actEntries);
    }

    private Entry entry(EventId eventId, String account, long amount) {
        return new Entry(eventId, new AccountId(eventId.getYearId(), account),
                new Currency(amount), new EntryStatus(EntryStatus.Status.ACTIVE,
                        EntryEvent.ORIGINAL));
    }
}
