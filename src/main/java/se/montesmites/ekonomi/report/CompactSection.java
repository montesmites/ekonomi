package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

public class CompactSection implements Section {
    private final Section section;

    public CompactSection(Section section) {
        this.section = section;
    }

    @Override
    public Stream<Row> stream() {
        Row title = section.streamTitle().findFirst().get();
        Row total = section.streamFooter().findFirst().get();
        Row row = new Row() {
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
        return Stream.of(row, new EmptyRow());
    }
}
