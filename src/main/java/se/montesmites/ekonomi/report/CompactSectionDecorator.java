package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.util.stream.Stream;

public class CompactSectionDecorator implements SectionDecorator {

  @Override
  public Section decorate(Section section) {
    return new Section() {
      @Override
      public Stream<Row> stream() {
        return Stream.of(createRow(section), Row.empty());
      }
    };
  }

  private Row createRow(Section section) {
    var title = section.header().stream().findFirst().orElseThrow();
    var total = section.streamFooter().findFirst().orElseThrow();
    return new RowWithGranularFormatters() {
      @Override
      public String formatDescription() {
        return title.format(DESCRIPTION);
      }

      @Override
      public String formatMonth(Column column) {
        return total.format(column);
      }

      @Override
      public String formatTotal() {
        return total.format(TOTAL);
      }

      @Override
      public String formatAverage() {
        return total.format(AVERAGE);
      }
    };
  }
}
