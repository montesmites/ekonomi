package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Footer {

  static Footer empty() {
    return Stream::empty;
  }

  static Footer of(Row row) {
    return () -> Stream.of(row);
  }

  static Footer of(RowAggregator rowAggregator) {
    return () -> Stream.of(rowAggregator.aggregate());
  }

  default Footer add(Row row) {
    return () -> Stream.concat(this.stream(), Stream.of(row));
  }

  Stream<Row> stream();
}
