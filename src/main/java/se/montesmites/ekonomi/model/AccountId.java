package se.montesmites.ekonomi.model;

import java.util.Objects;

public class AccountId {

    private final YearId yearId;
    private final String id;

    public AccountId(YearId yearId, String id) {
        this.yearId = yearId;
        this.id = id;
    }

    public YearId getYearId() {
        return yearId;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.yearId);
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final AccountId other = (AccountId) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.yearId, other.yearId);
    }

    @Override
    public String toString() {
        return "AccountId{" + "yearId=" + yearId + ", id=" + id + '}';
    }
}
