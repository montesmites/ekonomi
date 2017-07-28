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

public class Organization {

    public static Organization fromPath(Path path) {
        Parser p = new Parser(path);
        OrganizationBuilder b = new OrganizationBuilder();
        b.setEvents(p.parse(EVENTS));
        return b.build();
    }
    
    private final Map<EventId, Event> eventsById;
    
    public Organization(Collection<Event> events) {
        this.eventsById = events.stream().collect(toMap(Event::getEventId, identity()));;
    }

    public Optional<Event> getEvent(EventId eventId) {
        return Optional.ofNullable(eventsById.get(eventId));
    }    
}
