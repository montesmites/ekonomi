package se.montesmites.ekonomi.organization;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import se.montesmites.ekonomi.model.Year;

public class Organization {

    public static Organization fromPath(Path path) {
        Parser p = new Parser(path);
        return new Organization(
                p.parse(EVENTS),
                p.parse(YEARS));
    }

    private final Map<java.time.Year, Year> yearsByYear;
    private final Map<EventId, Event> eventsByEventId;

    private Organization(
            Collection<Event> events,
            Collection<Year> years) {
        this.yearsByYear = years.stream()
                .collect(toMap(Year::getYear, identity()));
        this.eventsByEventId = events.stream()
                .collect(toMap(Event::getEventId, identity()));
    }

    public Optional<Year> getYear(java.time.Year year) {
        return Optional.ofNullable(yearsByYear.get(year));
    }

    public Optional<Event> getEvent(EventId eventId) {
        return Optional.ofNullable(eventsByEventId.get(eventId));
    }
}
