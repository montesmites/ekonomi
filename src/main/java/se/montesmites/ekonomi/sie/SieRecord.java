package se.montesmites.ekonomi.sie;

import java.util.Collection;
import java.util.List;

public interface SieRecord {

  static SieRecord of(SieFileLine line) {
    return SieRecord.of(line, List.of());
  }

  static SieRecord of(SieFileLine line, Collection<SieRecord> subrecords) {
    var subrecs = List.copyOf(subrecords);
    return new SieRecord() {
      @Override
      public SieFileLine getLine() {
        return line;
      }

      @Override
      public List<SieRecord> getSubrecords() {
        return subrecs;
      }

      @Override
      public String toString() {
        return String.format("SieRecord(%s, %s)", line, subrecords);
      }
    };
  }

  SieFileLine getLine();

  List<SieRecord> getSubrecords();
}
