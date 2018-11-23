package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

interface Header {
    static Header empty() {
        return Stream::empty;
    }

    static Header of(Row row) {
        return () -> Stream.of(row);
    }

    static Header of(TitleRow titleRow) {
        return () -> Stream.of(titleRow);
    }

    static Header of(HeaderRow headerRow) {
        return () -> Stream.of(headerRow);
    }

    default Header add(Row row) {
        return () -> Stream.concat(this.stream(), Stream.of(row));
    }

    Stream<Row> stream();
}
