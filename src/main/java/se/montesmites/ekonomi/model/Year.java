package se.montesmites.ekonomi.model;

import java.time.LocalDate;
import java.util.Objects;

public class Year {
    private final YearId yearid;
    private final String year;
    private final LocalDate from;
    private final LocalDate to;

    public Year(YearId yearid, String year, LocalDate from, LocalDate to) {
        this.yearid = yearid;
        this.year = year;
        this.from = from;
        this.to = to;
    }

    public YearId getYearid() {
        return yearid;
    }

    public String getYear() {
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
        hash = 67 * hash + Objects.hashCode(this.yearid);
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
        if (!Objects.equals(this.yearid, other.yearid)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        return Objects.equals(this.to, other.to);
    }
}
