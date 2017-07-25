package se.montesmites.ekonomi.model;

import java.util.Objects;

public class YearId {

    private final String id;

    public YearId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        final YearId other = (YearId) obj;
        return Objects.equals(this.id, other.id);
    }
}
