package se.montesmites.ekonomi.sie.record.types;

import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeRES extends DefaultSieRecord {

  private final int yearId;
  private final String accountId;
  private final Currency balance;

  public TypeRES(SieRecord record, int yearId, String accountId, Currency balance) {
    super(record.getLine(), record.getLabel(), record.recordData(), record.getSubrecords());
    this.yearId = yearId;
    this.accountId = accountId;
    this.balance = balance;
  }

  public int getYearId() {
    return yearId;
  }

  public String getAccountId() {
    return accountId;
  }

  public Currency getBalance() {
    return balance;
  }
}
