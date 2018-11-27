package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Footer {

  static Footer empty() {
    return Stream::empty;
  }

  static Footer of(Row row) {
    return () -> Stream.of(row);
  }

  Stream<Row> stream();
}
