package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Report {

  public final static int DESCRIPTION_WIDTH = 30;

  public static Report empty() {
    return new Report(Stream::empty);
  }

  private final Supplier<Stream<Section>> sections;

  public Report(Supplier<Stream<Section>> sections) {
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
    return section -> section.stream().flatMap(renderRow());
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
        return "%-" + DESCRIPTION_WIDTH + "s";
      case TOTAL:
        return "%16s";
      case AVERAGE:
        return "%16s";
      default:
        return "%13s";
    }
  }
}
