package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

import static se.montesmites.ekonomi.report.Column.*;

public class CompactSectionDecorator implements SectionDecorator {
    @Override
    public Section decorate(Section section) {
        return new Section() {
            @Override
            public Stream<Row> stream() {
                return Stream.of(createRow(section), new EmptyRow());
            }
        };
    }

    private Row createRow(Section section) {
        var title = section.streamTitle().findFirst().orElseThrow();
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
