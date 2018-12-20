package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Stream;

public interface Footer {

  static Footer empty() {
    return Stream::empty;
  }

  static Footer of(Row row) {
    return () -> Stream.of(row);
  }

  static Footer of(List<Row> rows) {
    return rows::stream;
  }

  Stream<Row> stream();

  default String asString(String delimiter) {
    return stream().map(Row::asString).collect(joining(delimiter));
  }
}
