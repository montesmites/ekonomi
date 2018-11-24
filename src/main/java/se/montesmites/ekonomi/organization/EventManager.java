package se.montesmites.ekonomi.organization;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;

public class EventManager {

  private final Map<EventId, Event> eventsByEventId;

  EventManager(Stream<Event> events) {
    this.eventsByEventId = events.collect(toMap(Event::getEventId, identity()));
  }

  public Optional<Event> getEvent(EventId eventId) {
    return Optional.ofNullable(eventsByEventId.get(eventId));
  }
}
