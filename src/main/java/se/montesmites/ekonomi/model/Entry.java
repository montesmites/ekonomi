package se.montesmites.ekonomi.model;

import java.util.Objects;

public class Entry {

    private final EventId eventId;
    private final AccountId accountId;
    private final Currency amount;
    private final EntryStatus status;

    public Entry(EventId eventId, AccountId accountId, Currency amount, EntryStatus status) {
        this.eventId = eventId;
        this.accountId = accountId;
        this.amount = amount;
        this.status = status;
    }

    public EventId getEventId() {
        return eventId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public Currency getAmount() {
        return amount;
    }

    public EntryStatus getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.eventId);
        hash = 47 * hash + Objects.hashCode(this.accountId);
        hash = 47 * hash + Objects.hashCode(this.amount);
        hash = 47 * hash + Objects.hashCode(this.status);
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
        final Entry other = (Entry) obj;
        if (!Objects.equals(this.eventId, other.eventId)) {
            return false;
        }
        if (!Objects.equals(this.accountId, other.accountId)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        return Objects.equals(this.status, other.status);
    }

    @Override
    public String toString() {
        return "Entry{" + "eventId=" + eventId + ", accountId=" + accountId + ", amount=" + amount + ", status=" + status + '}';
    }
}
