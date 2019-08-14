package se.montesmites.ekonomi.sie.record;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import se.montesmites.ekonomi.sie.file.SieFileLine;
import se.montesmites.ekonomi.sie.record.types.SieRecordType;

public abstract class SieRecord {

  private static final Pattern LABEL_RECORD_DATA_PATTERN =
      Pattern.compile("\\s*#\\s*(\\w+)\\s+(.*)");

  public static final class InvalidSieRecord extends SieRecord {

    public static InvalidSieRecord of(SieFileLine line, List<SieRecord> subrecords) {
      return new InvalidSieRecord(line, subrecords);
    }

    private final SieFileLine line;
    private final List<SieRecord> subrecords;

    private InvalidSieRecord(SieFileLine line, List<SieRecord> subrecords) {
      this.line = line;
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
      throw new UnsupportedOperationException();
    }

    @Override
    public SieRecordData recordData() {
      throw new UnsupportedOperationException();
    }
  }

  public static SieRecord of(SieFileLine line) {
    return SieRecord.of(line, List.of());
  }

  public static SieRecord of(SieFileLine line, Collection<SieRecord> subrecords) {
    var subrecs = List.copyOf(subrecords);
    var matcher = LABEL_RECORD_DATA_PATTERN.matcher(line.getLine());
    if (!matcher.find()) {
      return new InvalidSieRecord(line, subrecs);
    } else {
      var label = matcher.group(1);
      var recorddata = SieRecordData.of(matcher.group(2));
      var record = new DefaultSieRecord(line, label, recorddata, subrecs);
      return SieRecordType.specialize(record);
    }
  }

  public abstract SieFileLine getLine();

  public abstract List<SieRecord> getSubrecords();

  public abstract String getLabel();

  public abstract SieRecordData recordData();
}
