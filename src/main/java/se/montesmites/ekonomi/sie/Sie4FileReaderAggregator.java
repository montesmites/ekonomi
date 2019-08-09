package se.montesmites.ekonomi.sie;

import java.util.ArrayList;
import java.util.List;

class Sie4FileReaderAggregator {

  static Sie4FileReaderAggregator empty() {
    return new Sie4FileReaderAggregator();
  }

  private List<SieRecord> records = new ArrayList<>();
  private SieFileLine parent;
  private List<SieRecord> children;
  private boolean isChild = false;

  private Sie4FileReaderAggregator() {
    this.children = new ArrayList<>();
  }

  private Sie4FileReaderAggregator(List<SieRecord> records) {
    this();
    this.records = records;
  }

  Sie4FileReaderAggregator accept(SieFileLine line) {
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

  Sie4FileReaderAggregator finish() {
    addRecord();
    return this;
  }

  List<SieRecord> retrieveRecords() {
    return List.copyOf(records);
  }

  Sie4FileReaderAggregator merge(Sie4FileReaderAggregator that) {
    this.finish();
    that.finish();
    var allRecords = new ArrayList<>(this.records);
    allRecords.addAll(that.records);
    return new Sie4FileReaderAggregator(allRecords);
  }
}
