package se.montesmites.ekonomi.sie;

public class SieFileLine {

  static SieFileLine of(String line) {
    switch (line.trim()) {
      case "{":
        return new SieFileLine(SieFileLineType.BEGIN_SUBRECORDS, line);
      case "}":
        return new SieFileLine(SieFileLineType.END_SUBRECORDS, line);
      default:
        return new SieFileLine(SieFileLineType.PROSAIC, line);
    }
  }

  private final SieFileLineType type;
  private final String line;

  private SieFileLine(SieFileLineType type, String line) {
    this.type = type;
    this.line = line;
  }

  public SieFileLineType getType() {
    return type;
  }

  public String getLine() {
    return line;
  }

  @Override
  public String toString() {
    return String.format("SieFileLine(%s, %s)", type, line);
  }
}
