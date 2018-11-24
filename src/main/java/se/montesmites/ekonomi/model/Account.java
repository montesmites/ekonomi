package se.montesmites.ekonomi.model;

import java.util.Objects;

public class Account {

  private final AccountId accountId;
  private final String description;
  private final AccountStatus accountStatus;

  public Account(AccountId accountId, String description, AccountStatus accountStatus) {
    this.accountId = accountId;
    this.description = description;
    this.accountStatus = accountStatus;
  }

  public AccountId getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }

  public AccountStatus getAccountStatus() {
    return accountStatus;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + Objects.hashCode(this.accountId);
    hash = 23 * hash + Objects.hashCode(this.description);
    hash = 23 * hash + Objects.hashCode(this.accountStatus);
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
    final Account other = (Account) obj;
    if (!Objects.equals(this.description, other.description)) {
      return false;
    }
    if (!Objects.equals(this.accountId, other.accountId)) {
      return false;
    }
    return Objects.equals(this.accountStatus, other.accountStatus);
  }

  @Override
  public String toString() {
    return "Account{"
        + "accountId="
        + accountId
        + ", description="
        + description
        + ", accountStatus="
        + accountStatus
        + '}';
  }
}
