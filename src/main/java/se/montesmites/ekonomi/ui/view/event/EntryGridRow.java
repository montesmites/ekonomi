package se.montesmites.ekonomi.ui.view.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName;

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
  private Amount amount;
  private String qualifier;
  private String name;

  public static EntryGridRow empty() {
    return new EntryGridRow();
  }

  public static EntryGridRow from(
      EntryDataAndAccountQualifierAndName entryDataAndAccountQualifierAndName) {
    return new EntryGridRow(
        entryDataAndAccountQualifierAndName.entryData().rowNo(),
        entryDataAndAccountQualifierAndName.entryData().entryId(),
        entryDataAndAccountQualifierAndName.entryData().eventId(),
        entryDataAndAccountQualifierAndName.entryData().rowNo(),
        entryDataAndAccountQualifierAndName.entryData().accountId(),
        entryDataAndAccountQualifierAndName.entryData().amount(),
        entryDataAndAccountQualifierAndName.accountQualifierAndName().qualifier().qualifier(),
        entryDataAndAccountQualifierAndName.accountQualifierAndName().name());
  }
}
