package se.montesmites.ekonomi.ui.view.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName;
import se.montesmites.ekonomi.ui.model.DebitCreditAmount;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EntryGridRow {

  @EqualsAndHashCode.Include private int index;
  private Long entryId;
  private Long eventId;
  private Integer rowNo;
  private Long accountId;
  private DebitCreditAmount amount;
  private String qualifier;
  private String name;

  public static EntryGridRow empty() {
    return new EntryGridRow().amount(DebitCreditAmount.empty());
  }

  public static EntryGridRow from(
      EntryDataAndAccountQualifierAndName entryDataAndAccountQualifierAndName) {
    return new EntryGridRow(
        entryDataAndAccountQualifierAndName.entryData().rowNo(),
        entryDataAndAccountQualifierAndName.entryData().entryId(),
        entryDataAndAccountQualifierAndName.entryData().eventId(),
        entryDataAndAccountQualifierAndName.entryData().rowNo(),
        entryDataAndAccountQualifierAndName.entryData().accountId(),
        DebitCreditAmount.from(entryDataAndAccountQualifierAndName.entryData().amount()),
        entryDataAndAccountQualifierAndName.accountQualifierAndName().qualifier().qualifier(),
        entryDataAndAccountQualifierAndName.accountQualifierAndName().name());
  }
}
