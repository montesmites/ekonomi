package se.montesmites.ekonomi.model.tuple;

import se.montesmites.ekonomi.model.AccountId;

import java.time.YearMonth;
import java.util.Objects;

public class YearMonthAccountIdTuple {

    private final YearMonth yearMonth;
    private final AccountId accountId;

    public YearMonthAccountIdTuple(YearMonth yearMonth, AccountId accountId) {
        this.yearMonth = yearMonth;
        this.accountId = accountId;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.yearMonth);
        hash = 53 * hash + Objects.hashCode(this.accountId);
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
        final YearMonthAccountIdTuple other = (YearMonthAccountIdTuple) obj;
        if (!Objects.equals(this.yearMonth, other.yearMonth)) {
            return false;
        }
        return Objects.equals(this.accountId, other.accountId);
    }

    @Override
    public String toString() {
        return "YearMonthAccountIdTuple{" + "yearMonth=" + yearMonth + ", accountId=" + accountId + '}';
    }
}
