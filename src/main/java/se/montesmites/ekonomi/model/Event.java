package se.montesmites.ekonomi.model;

import java.time.LocalDate;

public record Event(EventId eventId, LocalDate date, String description,
                    LocalDate registrationDate) {

}
