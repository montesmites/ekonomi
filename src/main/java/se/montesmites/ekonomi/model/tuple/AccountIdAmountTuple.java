package se.montesmites.ekonomi.model.tuple;

import java.util.Objects;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.Entry;

public class AccountIdAmountTuple {

  private final AccountId accountId;
  private final Currency amount;

  public AccountIdAmountTuple(Entry entry) {
    this(entry.getAccountId(), entry.getAmount());
  }

  public AccountIdAmountTuple(AccountId accountId, Currency amount) {
    this.accountId = accountId;
    this.amount = amount;
  }

  public AccountId getAccountId() {
    return accountId;
  }

  public Currency getAmount() {
    return amount;
  }

  private boolean hasSameAccountId(AccountIdAmountTuple that) {
    return this.getAccountId().equals(that.getAccountId());
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.accountId);
    hash = 97 * hash + Objects.hashCode(this.amount);
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
    final AccountIdAmountTuple other = (AccountIdAmountTuple) obj;
    if (!Objects.equals(this.accountId, other.accountId)) {
      return false;
    }
    return Objects.equals(this.amount, other.amount);
  }

  @Override
  public String toString() {
    return "AccountAmount{" + "accountId=" + accountId + ", amount=" + amount + '}';
  }
}
