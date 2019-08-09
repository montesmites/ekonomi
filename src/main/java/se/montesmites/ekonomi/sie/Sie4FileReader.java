package se.montesmites.ekonomi.sie;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class Sie4FileReader {

  private static final class SieFileLineAccumulator {

    private List<SieRecord> records = new ArrayList<>();
    private SieFileLine parent;
    private List<SieRecord> children;
    private boolean isChild = false;

    private SieFileLineAccumulator() {
      this.children = new ArrayList<>();
    }

    private SieFileLineAccumulator(List<SieRecord> records) {
      this();
      this.records = records;
    }

    private SieFileLineAccumulator accept(SieFileLine line) {
      if (line.getType() == SieFileLineType.BEGIN_SUBRECORDS) {
        this.isChild = true;
      } else if (line.getType() == SieFileLineType.END_SUBRECORDS) {
        this.isChild = false;
      } else if (line.getType() == SieFileLineType.PROSAIC) {
        if (isChild) {
          children.add(SieRecord.of(line));
        } else {
          addRecord();
          this.parent = line;
          this.children = new ArrayList<>();
          this.isChild = false;
        }
      } else {
        throw new IllegalStateException();
      }
      return this;
    }

    private void addRecord() {
      if (parent != null) {
        records.add(SieRecord.of(parent, List.copyOf(children)));
      }
    }

    private SieFileLineAccumulator finish() {
      addRecord();
      return this;
    }

    private List<SieRecord> retrieveRecords() {
      return List.copyOf(records);
    }

    private SieFileLineAccumulator merge(SieFileLineAccumulator that) {
      this.finish();
      that.finish();
      var allRecords = new ArrayList<>(this.records);
      allRecords.addAll(that.records);
      return new SieFileLineAccumulator(allRecords);
    }
  }

  private static final String CHARSET_NAME = "IBM437";
  private static final Charset CHARSET = Charset.availableCharsets().get(CHARSET_NAME);

  List<SieRecord> read(Path path) {
    try (var lines = Files.lines(path, CHARSET)) {
      return lines
          .filter(not(String::isBlank))
          .map(SieFileLine::of)
          .reduce(
              new SieFileLineAccumulator(),
              SieFileLineAccumulator::accept,
              SieFileLineAccumulator::merge)
          .finish()
          .retrieveRecords();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
