package se.montesmites.ekonomi.model;

import java.time.LocalDate;

public record Year(YearId yearId, java.time.Year year, LocalDate from, LocalDate to) {

}
