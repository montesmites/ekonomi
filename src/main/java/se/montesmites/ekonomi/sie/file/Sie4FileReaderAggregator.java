package se.montesmites.ekonomi.sie.file;

import static java.util.Objects.requireNonNull;
import static se.montesmites.ekonomi.sie.util.ListUtil.append;
import static se.montesmites.ekonomi.sie.util.ListUtil.concat;

import java.util.List;
import se.montesmites.ekonomi.sie.record.SieRecord;

abstract class Sie4FileReaderAggregator {

  private static final class OrphanAggregator extends Sie4FileReaderAggregator {

    private final List<SieRecord> records;

    private OrphanAggregator() {
      this(List.of());
    }

    private OrphanAggregator(List<SieRecord> records) {
      this.records = List.copyOf(records);
    }

    @Override
    Sie4FileReaderAggregator aggregate(SieFileLine line) {
      return new ParentAggregator(records, line);
    }

    @Override
    List<SieRecord> retrieveRecords() {
      return List.copyOf(records);
    }
  }

  private static final class ParentAggregator extends Sie4FileReaderAggregator {

    private final List<SieRecord> records;
    private final SieFileLine previousLine;

    private ParentAggregator(List<SieRecord> records, SieFileLine previousLine) {
      this.records = List.copyOf(records);
      this.previousLine = requireNonNull(previousLine);
    }

    @Override
    Sie4FileReaderAggregator aggregate(SieFileLine line) {
      if (line.getType() == SieFileLineType.BEGIN_SUBRECORDS) {
        return new ChildAggregator(records, previousLine);
      } else {
        return new ParentAggregator(append(records, retrieveRecord()), line);
      }
    }

    @Override
    List<SieRecord> retrieveRecords() {
      return append(records, retrieveRecord());
    }

    private SieRecord retrieveRecord() {
      return SieRecord.of(previousLine);
    }
  }

  private static final class ChildAggregator extends Sie4FileReaderAggregator {

    private final List<SieRecord> records;
    private final SieFileLine parent;
    private final List<SieRecord> children;

    private ChildAggregator(List<SieRecord> records, SieFileLine parent) {
      this(records, parent, List.of());
    }

    private ChildAggregator(List<SieRecord> records, SieFileLine parent, List<SieRecord> children) {
      this.records = List.copyOf(records);
      this.parent = parent;
      this.children = List.copyOf(children);
    }

    @Override
    Sie4FileReaderAggregator aggregate(SieFileLine line) {
      if (line.getType() == SieFileLineType.END_SUBRECORDS) {
        return new OrphanAggregator(append(records, retrieveRecord()));
      } else {
        return new ChildAggregator(records, parent, append(children, SieRecord.of(line)));
      }
    }

    @Override
    List<SieRecord> retrieveRecords() {
      return append(List.copyOf(records), retrieveRecord());
    }

    private SieRecord retrieveRecord() {
      return SieRecord.of(parent, children);
    }
  }

  static Sie4FileReaderAggregator empty() {
    return new OrphanAggregator();
  }

  abstract Sie4FileReaderAggregator aggregate(SieFileLine line);

  abstract List<SieRecord> retrieveRecords();

  Sie4FileReaderAggregator merge(Sie4FileReaderAggregator that) {
    return new OrphanAggregator(concat(this.retrieveRecords(), that.retrieveRecords()));
  }
}
