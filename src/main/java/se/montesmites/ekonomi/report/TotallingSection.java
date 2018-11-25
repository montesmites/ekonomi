package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class TotallingSection implements Section {

  private final String title;
  private final List<Section> sections;

  public TotallingSection(String title, List<Section> sections) {
    this.title = title;
    this.sections = sections;
  }

  @Override
  public Header header() {
    return Header.of(() -> title).add(SHORT_MONTHS_HEADER);
  }

  @Override
  public Footer footer() {
    return Footer.of(
        RowAggregator.of(() -> sections.stream().flatMap(streamWrappedBody())).aggregate());
  }

  private Function<Section, Stream<RowWithAmounts>> streamWrappedBody() {
    return (Section section) -> section.body().stream().flatMap(wrapRow(section));
  }

  private Function<RowWithAmounts, Stream<RowWithAmounts>> wrapRow(Section section) {
    return row -> Stream.of(wrapSectionRow(section, row));
  }

  protected RowWithAmounts wrapSectionRow(Section section, RowWithAmounts row) {
    return row;
  }

  @Override
  public Body body() {
    return Body.empty();
  }
}
