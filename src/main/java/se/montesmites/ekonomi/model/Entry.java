package se.montesmites.ekonomi.model;

public record Entry(
    EventId eventId, int rowNo, AccountId accountId, Currency amount, EntryStatus status) {}
