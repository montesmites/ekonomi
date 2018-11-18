package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static se.montesmites.ekonomi.report.HeaderRow.HeaderType.HEADER_TYPE_SHORT_MONTHS;

public interface AccumulatingSection extends Section {
    static Section of(String title, Supplier<Stream<Row>> bodyRows) {
        return Section.of(() -> title, () -> HEADER_TYPE_SHORT_MONTHS, bodyRows, Optional.empty());
    }
}
