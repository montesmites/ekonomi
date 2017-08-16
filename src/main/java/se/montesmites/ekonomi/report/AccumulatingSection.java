package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class AccumulatingSection extends DefaultSection {

    public AccumulatingSection(String title, Supplier<Stream<Row>> bodyRows) {
        super(title, bodyRows);
    }

    @Override
    public Stream<Row> streamFooter() {
        return Stream.empty();
    }
}
