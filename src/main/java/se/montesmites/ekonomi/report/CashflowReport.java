package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CashflowReport {

  private static Stream<RowWithAmounts> bodyRows(CashflowDataFetcher fetcher, java.time.Year year) {
    return fetcher
        .streamAccountIds(year)
        .map(
            accountId ->
                new DefaultRowWithAccounts(
                    fetcher, Stream.of(accountId).collect(toList()), year, accountId.getId()));
  }

  private final Supplier<Stream<Section>> sections;

  CashflowReport(CashflowDataFetcher fetcher, java.time.Year year) {
    this(
        fetcher,
        year,
        () -> {
          var header = Header.of(() -> "Unspecified Accounts").add(SHORT_MONTHS_HEADER);
          var body = Body.of(() -> bodyRows(fetcher, year));
          var footer = Footer.of(body.aggregate());
          return Stream.of(Section.of(header, body, footer));
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
    return renderSections();
  }

  private List<String> renderSections() {
    return streamSections().flatMap(streamSectionRows()).collect(toList());
  }

  private Function<Section, Stream<? extends String>> streamSectionRows() {
    return section -> Stream.concat(section.stream(), Stream.of(Row.empty())).flatMap(renderRow());
  }

  private Function<Row, Stream<? extends String>> renderRow() {
    return row ->
        Column.stream()
            .map(column -> format(row, column))
            .collect(collectingAndThen(joining(), Stream::of));
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
