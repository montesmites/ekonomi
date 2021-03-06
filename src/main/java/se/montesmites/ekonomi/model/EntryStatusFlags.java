package se.montesmites.ekonomi.model;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static se.montesmites.ekonomi.model.EntryEvent.DELETED;
import static se.montesmites.ekonomi.model.EntryEvent.DELETE_CANCELLED;
import static se.montesmites.ekonomi.model.EntryEvent.INSERTED;
import static se.montesmites.ekonomi.model.EntryEvent.NOT_REGISTERED;
import static se.montesmites.ekonomi.model.EntryEvent.ORIGINAL;
import static se.montesmites.ekonomi.model.EntryEvent.REINSERTED;
import static se.montesmites.ekonomi.model.EntryStatus.Status.ACTIVE;
import static se.montesmites.ekonomi.model.EntryStatus.Status.PASSIVE;

import java.util.List;
import java.util.Map;

class EntryStatusFlags {

  static final Map<String, EntryStatusFlags> ENTRY_STATUS_FLAGS =
      List.of(
              new EntryStatusFlags(
                  "T   ", "Original, and then untouched", new EntryStatus(ACTIVE, ORIGINAL)),
              new EntryStatusFlags(
                  "T  F",
                  "Original, and then untouched (unclear what's different from T___",
                  new EntryStatus(ACTIVE, ORIGINAL)),
              new EntryStatusFlags(
                  "T  T",
                  "Inserted after original registration",
                  new EntryStatus(ACTIVE, INSERTED)),
              new EntryStatusFlags(
                  "T FT",
                  "Original, then deleted, then reinserted",
                  new EntryStatus(ACTIVE, ORIGINAL, DELETED, REINSERTED)),
              new EntryStatusFlags(
                  "TTTT",
                  "Inserted after original registration, then deleted",
                  new EntryStatus(PASSIVE, INSERTED, DELETED)),
              new EntryStatusFlags(
                  "TTT ",
                  "Original, then deleted (unclear what's different from TT__)",
                  new EntryStatus(PASSIVE, ORIGINAL, DELETED)),
              new EntryStatusFlags(
                  "TT  ",
                  "Original, then deleted (unclear what's different from TTT_)",
                  new EntryStatus(PASSIVE, ORIGINAL, DELETED)),
              new EntryStatusFlags(
                  "   T",
                  "Cancelled from original registration",
                  new EntryStatus(PASSIVE, DELETED)),
              new EntryStatusFlags(
                  "T F ",
                  "Original, then cancelled deletion",
                  new EntryStatus(ACTIVE, ORIGINAL, DELETE_CANCELLED)),
              new EntryStatusFlags(
                  "F   ", "Not registered", new EntryStatus(PASSIVE, NOT_REGISTERED)),
              new EntryStatusFlags(
                  "    ", "Not registered", new EntryStatus(PASSIVE, NOT_REGISTERED)))
          .stream()
          .collect(toMap(EntryStatusFlags::getFlags, identity()));

  private final String flags;
  private final String descr;
  private final EntryStatus status;

  private EntryStatusFlags(String flags, String descr, EntryStatus status) {
    this.flags = flags;
    this.descr = descr;
    this.status = status;
  }

  private String getFlags() {
    return flags;
  }

  @SuppressWarnings("unused")
  public String getDescr() {
    return descr;
  }

  EntryStatus getStatus() {
    return status;
  }
}
