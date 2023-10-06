package se.montesmites.ekonomi.db;

import se.montesmites.ekonomi.db.model.Amount;

public record EntryData(Long entryId, Long eventId, Integer rowNo, Long accountId, Amount amount) {}
