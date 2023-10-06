package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Report {

  public static final int DESCRIPTION_WIDTH = 31;
  private final Supplier<Stream<Section>> sections;

  public Report(Supplier<Stream<Section>> sections) {
    this.sections = sections;
  }

  public static Report empty() {
    return new Report(Stream::empty);
  }

  public Stream<Section> streamSections() {
    return sections.get();
  }

  public List<String> render() {
    return renderSections();
  }

  public List<String> renderWithNoTrailingEmptyRows() {
    var rows = new LinkedList<>(renderSections());
    while (rows.peekLast() != null
        && (rows.peekLast().isEmpty() || rows.peekLast().trim().isEmpty())) {
      rows.removeLast();
    }
    return List.copyOf(rows);
  }

  public void renderToFile(Report report, Path outputPath) {
    try (var writer = Files.newBufferedWriter(outputPath)) {
      Files.createDirectories(outputPath.getParent());
      var lines = report.renderWithNoTrailingEmptyRows();
      for (var i = 0; i < lines.size() - 1; i++) {
        writer.append(lines.get(i));
        writer.newLine();
      }
      writer.append(lines.get(lines.size() - 1));
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<String> renderSections() {
    return streamSections().flatMap(streamSectionRows()).toList();
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
    return switch (column.getColumnType()) {
      case DESCRIPTION -> "%-" + DESCRIPTION_WIDTH + "s";
      case TOTAL, AVERAGE -> "%16s";
      default -> "%13s";
    };
  }
}
