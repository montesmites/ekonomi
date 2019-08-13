package se.montesmites.ekonomi.sie.record.types;

import java.util.List;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.sie.file.SieFileLine;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;
import se.montesmites.ekonomi.sie.record.SieRecordData;

public class TypeIB extends DefaultSieRecord {

  private final int yearId;
  private final String accountId;
  private final Currency balance;

  public TypeIB(
      SieFileLine line, String label, SieRecordData recorddata, List<SieRecord> subrecords) {
    super(line, label, recorddata, subrecords);
    this.yearId = recorddata.get(0).asInt();
    this.accountId = recorddata.get(1).asString();
    this.balance = recorddata.get(2).asCurrency();
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
