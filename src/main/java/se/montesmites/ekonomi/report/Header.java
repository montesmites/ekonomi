package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Stream;

public interface Header {

  static Header empty() {
    return Stream::empty;
  }

  static Header of(Row row) {
    return () -> Stream.of(row);
  }

  static Header of(List<Row> rows) {
    return rows::stream;
  }

  default Header add(Row row) {
    return () -> Stream.concat(this.stream(), Stream.of(row));
  }

  Stream<Row> stream();

  default String asString(String delimiter) {
    return stream().map(Row::asString).collect(joining(delimiter));
  }
}
