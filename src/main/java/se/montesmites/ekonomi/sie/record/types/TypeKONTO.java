package se.montesmites.ekonomi.sie.record.types;

import java.util.List;
import se.montesmites.ekonomi.sie.file.SieFileLine;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;
import se.montesmites.ekonomi.sie.record.SieRecordData;

public class TypeKONTO extends DefaultSieRecord {

  private final String accountId;
  private final String description;

  public TypeKONTO(
      SieFileLine line, String label, SieRecordData recorddata, List<SieRecord> subrecords) {
    super(line, label, recorddata, subrecords);
    this.accountId = recorddata.get(0).asString();
    this.description = recorddata.get(1).asString();
  }

  public String getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }
}
