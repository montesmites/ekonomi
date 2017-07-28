package se.montesmites.ekonomi.model;

import java.util.Optional;

public enum AccountStatus {
    OPEN("open"),
    CLOSED("closed"),
    REMOVED("removed");

    public final static Optional<AccountStatus> parse(String removedFlag, String inactiveFlag) {
        if ("*".equals(removedFlag)) {
            return Optional.of(REMOVED);
        } else if ("T".equals(inactiveFlag)) {
            return Optional.of(CLOSED);
        } else if ("F".equals(inactiveFlag)) {
            return Optional.of(OPEN);
        } else {
            return Optional.of(OPEN);
        }
    }

    private final String description;

    private AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AccountStatus{" + "description=" + description + '}';
    }
}