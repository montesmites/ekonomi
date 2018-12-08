package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Header {

  static Header empty() {
    return Stream::empty;
  }

  static Header of(Row row) {
    return () -> Stream.of(row);
  }

  default Header add(Row row) {
    return () -> Stream.concat(this.stream(), Stream.of(row));
  }

  Stream<Row> stream();
}
