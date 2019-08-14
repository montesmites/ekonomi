package se.montesmites.ekonomi.sie.record;

import java.util.List;
import se.montesmites.ekonomi.sie.file.SieFileLine;

public class DefaultSieRecord extends SieRecord {

  private final SieFileLine line;
  private final String label;
  private final SieRecordData recorddata;
  private final List<SieRecord> subrecords;

  public DefaultSieRecord(
      SieFileLine line, String label, SieRecordData recorddata, List<SieRecord> subrecords) {
    this.line = line;
    this.label = label;
    this.recorddata = recorddata;
    this.subrecords = List.copyOf(subrecords);
  }

  @Override
  public SieFileLine getLine() {
    return line;
  }

  @Override
  public List<SieRecord> getSubrecords() {
    return subrecords;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public SieRecordData getRecordData() {
    return recorddata;
  }
}
