package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CashflowReport {

  private static Stream<Row> bodyRows(CashflowDataFetcher fetcher, java.time.Year year) {
    return fetcher
        .streamAccountIds(year)
        .map(
            accountId ->
                new DefaultRowWithAccounts(
                    fetcher, () -> Stream.of(accountId), year, accountId.getId()));
  }

  private final Supplier<Stream<Section>> sections;

  CashflowReport(CashflowDataFetcher fetcher, java.time.Year year) {
    this(
        fetcher,
        year,
        () -> {
          var bodyRows = (Supplier<Stream<Row>>) () -> bodyRows(fetcher, year);
          return Stream.of(
              Section.of(
                  () -> "Unspecified Accounts", SHORT_MONTHS_HEADER, bodyRows));
        });
  }

  public CashflowReport(
      CashflowDataFetcher fetcher, java.time.Year year, Supplier<Stream<Section>> sections) {
    this.sections = sections;
  }

  Stream<Section> streamSections() {
    return sections.get();
  }

  public List<String> render() {
    return streamSections()
        .flatMap(
            section ->
                section
                    .stream()
                    .flatMap(
                        row ->
                            Column.stream()
                                .map(column -> format(row, column))
                                .collect(collectingAndThen(joining(), Stream::of))))
        .collect(toList());
  }

  private String format(Row row, Column column) {
    return String.format(formatString(column), row.format(column));
  }

  private String formatString(Column column) {
    switch (column.getColumnType()) {
      case DESCRIPTION:
        return "%-30s";
      case TOTAL:
        return "%16s";
      case AVERAGE:
        return "%16s";
      default:
        return "%13s";
    }
  }
}
