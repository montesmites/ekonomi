package se.montesmites.ekonomi.report;

import java.util.List;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

public class CashflowReport {

    private final CashflowDataFetcher fetcher;
    private final java.time.Year year;

    public CashflowReport(CashflowDataFetcher fetcher, java.time.Year year) {
        this.fetcher = fetcher;
        this.year = year;
    }

    public Stream<Section> streamSections() {
        return Stream.of(new Section(fetcher, year));
    }

    public List<String> render() {
        return streamSections()
                .flatMap(section -> section.streamAllRows()
                .flatMap(row -> Column.stream()
                .map(column -> format(row, column))
                .collect(collectingAndThen(joining(), Stream::of)))).
                collect(toList());
    }

    private String format(Row row, Column column) {
        return String.format(formatString(column), row.getText(column));
    }

    private String formatString(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return "%-20s";
            case TOTAL:
                return "%20s\n";
            default:
                return "%20s";
        }
    }
}
