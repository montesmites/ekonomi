package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.Optional;

public interface AccumulatingSection extends Section {

  static Section of(String title, Body body) {
    return Section.of(Header.of(() -> title).add(SHORT_MONTHS_HEADER), body, Optional.empty());
  }
}
