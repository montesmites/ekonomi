package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultSection implements Section {

    private final TitleRow title;
    private final HeaderRow header;
    private final FooterRow footer;
    private final Supplier<Stream<Row>> bodyRows;

    public DefaultSection(String title, Supplier<Stream<Row>> bodyRows) {
        this.title = new DefaultTitleRow(title);
        this.header = new DefaultHeaderRow();
        this.footer = new DefaultFooterRow(this);
        this.bodyRows = bodyRows;
    }
    
    @Override
    public Stream<Row> streamTitle() {
        return Stream.of(this.title);
    }

    @Override
    public Stream<Row> streamHeader() {
        return Stream.of(header);
    }

    @Override
    public Stream<Row> streamFooter() {
        return Stream.of(footer, new EmptyRow());
    }

    @Override
    public Stream<Row> streamBody() {
        return bodyRows.get();
    }
}
