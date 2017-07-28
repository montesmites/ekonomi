package se.montesmites.ekonomi.model;

import java.time.LocalDate;
import java.util.Objects;

public class Event {

    private final EventId eventId;
    private final LocalDate date;
    private final String description;
    private final LocalDate registrationDate;

    public Event(EventId eventId, LocalDate date, String description, LocalDate registrationDate) {
        this.eventId = eventId;
        this.date = date;
        this.description = description;
        this.registrationDate = registrationDate;
    }

    public EventId getEventId() {
        return eventId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.eventId);
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + Objects.hashCode(this.registrationDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.eventId, other.eventId)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return Objects.equals(this.registrationDate, other.registrationDate);
    }

    @Override
    public String toString() {
        return "Event{" + "eventId=" + eventId + ", date=" + date + ", description=" + description + ", registrationDate=" + registrationDate + '}';
    }
}
