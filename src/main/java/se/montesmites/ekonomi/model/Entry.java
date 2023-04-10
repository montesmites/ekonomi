package se.montesmites.ekonomi.model;

public record Entry(EventId eventId, AccountId accountId, Currency amount, EntryStatus status) {}
