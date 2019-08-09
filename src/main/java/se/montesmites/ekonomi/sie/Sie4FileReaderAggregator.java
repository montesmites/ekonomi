package se.montesmites.ekonomi.sie;

import static se.montesmites.ekonomi.sie.Sie4FileReaderAggregator.AggregationStrategy.CHILD_RECORD_STRATEGY;
import static se.montesmites.ekonomi.sie.Sie4FileReaderAggregator.AggregationStrategy.PARENT_RECORD_STRATEGY;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

class Sie4FileReaderAggregator {

  enum AggregationStrategy {
    PARENT_RECORD_STRATEGY(
        (aggregator, line) -> {
          aggregator.addRecord();
          aggregator.parent = line;
          aggregator.children = new ArrayList<>();
          setParentRecordStrategy(aggregator);
        }),
    CHILD_RECORD_STRATEGY((aggregator, line) -> aggregator.children.add(SieRecord.of(line)));

    private final BiConsumer<Sie4FileReaderAggregator, SieFileLine> strategy;

    AggregationStrategy(BiConsumer<Sie4FileReaderAggregator, SieFileLine> strategy) {
      this.strategy = strategy;
    }

    final void accept(Sie4FileReaderAggregator aggregator, SieFileLine line) {
      strategy.accept(aggregator, line);
    }

    private static void setParentRecordStrategy(Sie4FileReaderAggregator aggregator) {
      aggregator.strategy = PARENT_RECORD_STRATEGY;
    }
  }

  static Sie4FileReaderAggregator empty() {
    return new Sie4FileReaderAggregator();
  }

  private List<SieRecord> records = new ArrayList<>();
  private SieFileLine parent;
  private List<SieRecord> children;
  private AggregationStrategy strategy = PARENT_RECORD_STRATEGY;

  private Sie4FileReaderAggregator() {
    this.children = new ArrayList<>();
  }

  private Sie4FileReaderAggregator(List<SieRecord> records) {
    this();
    this.records = records;
  }

  Sie4FileReaderAggregator accept(SieFileLine line) {
    if (line.getType() == SieFileLineType.BEGIN_SUBRECORDS) {
      this.strategy = CHILD_RECORD_STRATEGY;
    } else if (line.getType() == SieFileLineType.END_SUBRECORDS) {
      this.strategy = PARENT_RECORD_STRATEGY;
    } else if (line.getType() == SieFileLineType.PROSAIC) {
      this.strategy.accept(this, line);
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
