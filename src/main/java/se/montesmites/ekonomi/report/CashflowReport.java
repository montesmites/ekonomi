package se.montesmites.ekonomi.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public class CashflowReport {

    private static Set<AccountId> set(AccountId accountId) {
        Set<AccountId> set = new HashSet<>();
        set.add(accountId);
        return set;
    }

    private static Stream<BodyRow> bodyRows(CashflowDataFetcher fetcher, java.time.Year year) {
        return fetcher.streamAccountIds(year)
                .map(accountId
                        -> new DefaultBodyRow(
                        fetcher,
                        () -> set(accountId).stream(),
                        year,
                        accountId.getId()));
    }

    private final Supplier<Stream<Section>> sections;

    public CashflowReport(CashflowDataFetcher fetcher, java.time.Year year) {
        this(fetcher, year, ()
                -> Stream.of(
                        new Section(
                                "Unspecified Accounts",
                                year,
                                () -> bodyRows(fetcher, year))));
    }

    public CashflowReport(CashflowDataFetcher fetcher, java.time.Year year, Supplier<Stream<Section>> sections) {
        this.sections = sections;
    }

    public Stream<Section> streamSections() {
        return sections.get();
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
                return "%20s";
            default:
                return "%20s";
        }
    }
}
