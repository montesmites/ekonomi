package se.montesmites.ekonomi.sie.record.types;

import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeKONTO extends DefaultSieRecord {

  static TypeKONTO of(SieRecord record) {
    var accountId = record.getRecordData().get(0).asString();
    var description = record.getRecordData().get(1).asString();
    return new TypeKONTO(record, accountId, description);
  }

  private final String accountId;
  private final String description;

  private TypeKONTO(SieRecord record, String accountId, String description) {
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
