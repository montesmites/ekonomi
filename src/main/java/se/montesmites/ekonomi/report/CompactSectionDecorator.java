package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

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
        var title = section.streamTitle().findFirst().get();
        var total = section.streamFooter().findFirst().get();
        return new Row() {
            @Override
            public String formatDescription() {
                return title.formatText(DESCRIPTION);
            }

            @Override
            public String formatMonth(Column column) {
                return total.formatText(column);
            }

            @Override
            public String formatTotal() {
                return total.formatTotal();
            }

            @Override
            public String formatAverage() {
                return total.formatAverage();
            }
        };
    }
}
