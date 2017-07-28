package se.montesmites.ekonomi.model;

import java.time.LocalDate;
import java.util.Objects;

public class Year {

    private final YearId yearId;
    private final java.time.Year year;
    private final LocalDate from;
    private final LocalDate to;

    public Year(YearId yearId, java.time.Year year, LocalDate from, LocalDate to) {
        this.yearId = yearId;
        this.year = year;
        this.from = from;
        this.to = to;
    }

    public YearId getYearId() {
        return yearId;
    }

    public java.time.Year getYear() {
        return year;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.yearId);
        hash = 67 * hash + Objects.hashCode(this.year);
        hash = 67 * hash + Objects.hashCode(this.from);
        hash = 67 * hash + Objects.hashCode(this.to);
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
        final Year other = (Year) obj;
        if (!Objects.equals(this.year, other.year)) {
            return false;
        }
        if (!Objects.equals(this.yearId, other.yearId)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        return Objects.equals(this.to, other.to);
    }

    @Override
    public String toString() {
        return "Year{" + "yearid=" + yearId + ", year=" + year + ", from=" + from + ", to=" + to + '}';
    }
}
