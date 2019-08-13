package se.montesmites.ekonomi.sie.record.types;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import se.montesmites.ekonomi.sie.file.SieFileLine;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;
import se.montesmites.ekonomi.sie.record.SieRecordData;

public class TypeRAR extends DefaultSieRecord {

  private final int yearId;
  private final Year year;
  private final LocalDate start;
  private final LocalDate end;

  public TypeRAR(
      SieFileLine line, String label, SieRecordData recorddata, List<SieRecord> subrecords) {
    super(line, label, recorddata, subrecords);
    this.yearId = recorddata.get(0).asInt();
    this.start = recorddata.get(1).asDate();
    this.end = recorddata.get(2).asDate();
    this.year = Year.of(this.start.getYear());
  }

  public int getYearId() {
    return yearId;
  }

  public Year getYear() {
    return year;
  }

  public LocalDate getStart() {
    return start;
  }

  public LocalDate getEnd() {
    return end;
  }
}
