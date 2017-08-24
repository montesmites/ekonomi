package se.montesmites.ekonomi.organization;

import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class EventManager {
    private final Collection<Event> events;
    private final Map<EventId, Event> eventsByEventId;

    public EventManager(Stream<Event> events) {
        this.events = events.collect(toList());
        this.eventsByEventId = this.events.stream()
                .collect(toMap(Event::getEventId, identity()));
    }

    public Optional<Event> getEvent(EventId eventId) {
        return Optional.ofNullable(eventsByEventId.get(eventId));
    }
}
