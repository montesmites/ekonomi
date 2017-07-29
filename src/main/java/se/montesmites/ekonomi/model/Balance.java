package se.montesmites.ekonomi.model;

import java.util.Objects;

public class Balance {

    private final AccountId accountId;
    private final Currency balance;

    public Balance(AccountId accountId, Currency balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public Currency getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.accountId);
        hash = 71 * hash + Objects.hashCode(this.balance);
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
        final Balance other = (Balance) obj;
        if (!Objects.equals(this.accountId, other.accountId)) {
            return false;
        }
        return Objects.equals(this.balance, other.balance);
    }

    @Override
    public String toString() {
        return "Balance{" + "accountId=" + accountId + ", balance=" + balance + '}';
    }
}
