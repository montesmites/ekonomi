package se.montesmites.ekonomi.model;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record EntryStatus(Status status, List<EntryEvent> events) {

  public EntryStatus(boolean registered, boolean cancelled, boolean amended) {
    this(
        registered && !cancelled ? Status.ACTIVE : Status.PASSIVE,
        Stream.of(EntryEvent.ORIGINAL, EntryEvent.INSERTED, EntryEvent.DELETED)
            .flatMap(
                entryEvent ->
                    switch (entryEvent) {
                      case ORIGINAL -> Stream.of(EntryEvent.ORIGINAL);
                      case INSERTED -> amended ? Stream.of(EntryEvent.INSERTED) : Stream.empty();
                      case DELETED -> cancelled ? Stream.of(EntryEvent.DELETED) : Stream.empty();
                      default -> Stream.empty();
                    })
            .collect(toList()));
  }

  public EntryStatus(Status status, EntryEvent... events) {
    this(status, List.of(events));
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
      return Optional.of(entryStatusFlags.status());
    } else {
      return Optional.empty();
    }
  }

  public enum Status {
    ACTIVE,
    PASSIVE
  }
}
