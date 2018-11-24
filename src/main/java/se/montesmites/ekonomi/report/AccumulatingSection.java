package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface AccumulatingSection extends Section {

  static Section of(String title, Supplier<Stream<Row>> bodyRows) {
    return Section.of(Header.of(() -> title).add(SHORT_MONTHS_HEADER), bodyRows, Optional.empty());
  }
}
