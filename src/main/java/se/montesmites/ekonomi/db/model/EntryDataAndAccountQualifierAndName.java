package se.montesmites.ekonomi.db.model;

import java.math.BigDecimal;
import se.montesmites.ekonomi.db.EntryData;

public record EntryDataAndAccountQualifierAndName(
    EntryData entryData, AccountQualifierAndName accountQualifierAndName) {

  public EntryDataAndAccountQualifierAndName(
      Long entryId,
      Long eventId,
      Integer rowNo,
      Long accountId,
      BigDecimal amount,
      String qualifier,
      String name) {
    this(
        new EntryData(entryId, eventId, rowNo, accountId, new Amount(amount)),
        new AccountQualifierAndName(new AccountQualifier(qualifier), name));
  }
}
