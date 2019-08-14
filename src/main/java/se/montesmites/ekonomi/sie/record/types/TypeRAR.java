package se.montesmites.ekonomi.sie.record.types;

import java.time.LocalDate;
import java.time.Year;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeRAR extends DefaultSieRecord {

  private final int yearId;
  private final Year year;
  private final LocalDate start;
  private final LocalDate end;

  public TypeRAR(SieRecord record, int yearId, LocalDate start, LocalDate end) {
    super(record.getLine(), record.getLabel(), record.getRecordData(), record.getSubrecords());
    this.yearId = yearId;
    this.year = Year.of(start.getYear());
    this.start = start;
    this.end = end;
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
