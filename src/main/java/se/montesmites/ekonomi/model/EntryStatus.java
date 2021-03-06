package se.montesmites.ekonomi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntryStatus {

  public enum Status {
    ACTIVE,
    PASSIVE
  }

  public static Optional<EntryStatus> parse(String flags) {
    if (flags.length() == 4) {
      return eventify(flags);
    } else {
      return Optional.empty();
    }
  }

  private static Optional<EntryStatus> eventify(String flags) {
    var entryStatusFlags = EntryStatusFlags.ENTRY_STATUS_FLAGS.get(flags);
    if (entryStatusFlags != null) {
      return Optional.of(entryStatusFlags.getStatus());
    } else {
      return Optional.empty();
    }
  }

  private final Status status;
  private final List<EntryEvent> events;

  public EntryStatus(boolean registered, boolean cancelled, boolean amended) {
    this.status = registered && !cancelled ? Status.ACTIVE : Status.PASSIVE;
    this.events = new ArrayList<>();
    this.events.add(EntryEvent.ORIGINAL);
    if (amended) {
      this.events.add(EntryEvent.INSERTED);
    }
    if (cancelled) {
      this.events.add(EntryEvent.DELETED);
    }
  }

  public EntryStatus(Status status, EntryEvent... events) {
    this(status, List.of(events));
  }

  private EntryStatus(Status status, List<EntryEvent> events) {
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
    return this.status == other.status && Objects.equals(this.events, other.events);
  }

  @Override
  public String toString() {
    return "EntryStatus{" + "status=" + status + ", events=" + events + '}';
  }
}
