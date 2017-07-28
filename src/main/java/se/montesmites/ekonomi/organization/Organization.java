package se.montesmites.ekonomi.organization;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Year;

public class Organization {

    public static Organization fromPath(Path path) {
        Parser p = new Parser(path);
        return new Organization(
                p.parse(ENTRIES),
                p.parse(EVENTS),
                p.parse(YEARS));
    }

    private final Map<java.time.Year, Year> yearsByYear;
    private final Map<EventId, Event> eventsByEventId;
    private final Map<EventId, List<Entry>> entriesByEventId;

    private Organization(
            Collection<Entry> entries,
            Collection<Event> events,
            Collection<Year> years) {
        this.yearsByYear = years.stream()
                .collect(toMap(Year::getYear, identity()));
        this.eventsByEventId = events.stream()
                .collect(toMap(Event::getEventId, identity()));
        this.entriesByEventId = entries.stream()
                .collect(groupingBy(Entry::getEventId));
    }

    public Optional<Year> getYear(java.time.Year year) {
        return Optional.ofNullable(yearsByYear.get(year));
    }

    public Optional<Event> getEvent(EventId eventId) {
        return Optional.ofNullable(eventsByEventId.get(eventId));
    }

    public Optional<List<Entry>> getEntries(EventId eventId) {
        return Optional.ofNullable(entriesByEventId.get(eventId));
    }
}
