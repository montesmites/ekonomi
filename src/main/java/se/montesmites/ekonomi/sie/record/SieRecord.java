package se.montesmites.ekonomi.sie.record;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import se.montesmites.ekonomi.sie.file.SieFileLine;

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

  public static final class ValidSieRecord extends SieRecord {

    public static ValidSieRecord of(
        SieFileLine line, String label, SieRecordData recorddata, List<SieRecord> subrecords) {
      return new ValidSieRecord(line, label, recorddata, subrecords);
    }

    private final SieFileLine line;
    private final String label;
    private final SieRecordData recorddata;
    private final List<SieRecord> subrecords;

    private ValidSieRecord(
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
    public SieRecordData recordData() {
      return recorddata;
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
      return new ValidSieRecord(
          line, matcher.group(1), SieRecordData.of(matcher.group(2)), subrecs);
    }
  }

  private SieRecord() {
  }

  public abstract SieFileLine getLine();

  public abstract List<SieRecord> getSubrecords();

  public abstract String getLabel();

  public abstract SieRecordData recordData();
}
