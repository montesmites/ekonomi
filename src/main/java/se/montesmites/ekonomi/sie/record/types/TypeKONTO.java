package se.montesmites.ekonomi.sie.record.types;

import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeKONTO extends DefaultSieRecord {

  private final String accountId;
  private final String description;

  public TypeKONTO(SieRecord record, String accountId, String description) {
    super(record.getLine(), record.getLabel(), record.getRecordData(), record.getSubrecords());
    this.accountId = accountId;
    this.description = description;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }
}
