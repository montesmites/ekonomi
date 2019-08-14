package se.montesmites.ekonomi.sie.record.types;

import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeIB extends DefaultSieRecord {

  static TypeIB of(SieRecord record) {
    var yearId = record.getRecordData().get(0).asInt();
    var accountId = record.getRecordData().get(1).asString();
    var balance = record.getRecordData().get(2).asCurrency();
    return new TypeIB(record, yearId, accountId, balance);
  }

  private final int yearId;
  private final String accountId;
  private final Currency balance;

  private TypeIB(SieRecord record, int yearId, String accountId, Currency balance) {
    super(record.getLine(), record.getLabel(), record.getRecordData(), record.getSubrecords());
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
