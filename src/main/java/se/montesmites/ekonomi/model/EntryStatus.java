package se.montesmites.ekonomi.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntryStatus {

    public static enum Status {
        ACTIVE, PASSIVE;
    }
    
    public static Optional<EntryStatus> parse(String flags) {
        if (flags.length() == 4) {
            return eventify(flags);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryStatus> eventify(String flags) {
        final EntryStatusFlags esf = EntryStatusFlags.ENTRY_STATUS_FLAGS.get(flags);
        if (esf != null) {
            return Optional.of(esf.getStatus());
        } else {
            return Optional.empty();
        }
    }

    private final Status status;
    private final List<EntryEvent> events;

    public EntryStatus(Status status, EntryEvent... events) {
        this(status, Arrays.asList(events));
    }

    public EntryStatus(Status status, List<EntryEvent> events) {
        this.status = status;
        this.events = events;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.status);
        hash = 29 * hash + Objects.hashCode(this.events);
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
        final EntryStatus other = (EntryStatus) obj;
        if (this.status != other.status) {
            return false;
        }
        return Objects.equals(this.events, other.events);
    }

    @Override
    public String toString() {
        return "EntryStatus{" + "status=" + status + ", events=" + events + '}';
    }
}
