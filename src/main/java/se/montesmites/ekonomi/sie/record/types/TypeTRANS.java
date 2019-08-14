package se.montesmites.ekonomi.sie.record.types;

import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeTRANS extends DefaultSieRecord {

  static TypeTRANS of(SieRecord record) {
    var accountId = record.getRecordData().get(0).asString();
    var amount = record.getRecordData().get(2).asCurrency();
    return new TypeTRANS(record, accountId, amount);
  }

  private final String accountId;
  private final Currency amount;

  private TypeTRANS(SieRecord record, String accountId, Currency amount) {
    super(record.getLine(), record.getLabel(), record.getRecordData(), record.getSubrecords());
    this.accountId = accountId;
    this.amount = amount;
  }

  public String getAccountId() {
    return accountId;
  }

  public Currency getAmount() {
    return amount;
  }
}
