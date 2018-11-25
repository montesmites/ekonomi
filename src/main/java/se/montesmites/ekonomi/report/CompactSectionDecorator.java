package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.TOTAL;

public class CompactSectionDecorator implements SectionDecorator {

  @Override
  public Section decorate(Section section) {
    return Section.of(Header.empty(), Body.empty(), Footer.of(createRow(section)));
  }

  private Row createRow(Section section) {
    var title = section.header().stream().findFirst().orElseThrow();
    var total = section.footer().stream().findFirst().orElseThrow();
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
