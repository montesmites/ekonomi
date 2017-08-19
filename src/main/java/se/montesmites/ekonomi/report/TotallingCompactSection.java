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
        return Stream.of(column -> {
            switch (column) {
                case DESCRIPTION:
                    return title.getText(DESCRIPTION);
                default:
                    return total.getText(column);
            }
        });
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
