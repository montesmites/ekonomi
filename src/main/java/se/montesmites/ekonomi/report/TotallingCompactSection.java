package se.montesmites.ekonomi.report;

import java.util.List;
import java.util.stream.Stream;
import static se.montesmites.ekonomi.report.Column.*;

public class TotallingCompactSection extends TotallingSection {

    public TotallingCompactSection(String title, List<Section> sections) {
        super(title, sections);
    }

    @Override
    public Stream<Row> streamTitle() {
        final Row title = super.streamTitle().findFirst().get();
        final Row total = super.streamFooter().findFirst().get();
        final Row row = new Row() {
            @Override
            public String formatDescription() {
                return title.formatText(DESCRIPTION);
            }

            @Override
            public String formatMonth(Column column) {
                return total.formatText(column);
            }
        };
        return Stream.of(row);
    }

    @Override
    public Stream<Row> streamHeader() {
        return Stream.empty();
    }

    @Override
    public Stream<Row> streamFooter() {
        return Stream.of(new EmptyRow());
    }
}
