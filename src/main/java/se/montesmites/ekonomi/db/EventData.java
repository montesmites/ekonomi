package se.montesmites.ekonomi.db;

import java.time.LocalDate;

public record EventData(
    Long eventId, Long fiscalYearId, Integer eventNo, LocalDate date, String description) {}
