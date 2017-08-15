package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultSection implements Section {

    private final TitleRow title;
    private final HeaderRow header;
    private final FooterRow footer;
    private final Supplier<Stream<BodyRow>> bodyRows;

    public DefaultSection(String title, Supplier<Stream<BodyRow>> bodyRows) {
        this.title = new TitleRow(title);
        this.header = new HeaderRow();
        this.footer = new FooterRow(this);
        this.bodyRows = bodyRows;
    }
    
    @Override
    public Stream<TitleRow> streamTitle() {
        return Stream.of(this.title);
    }

    @Override
    public Stream<HeaderRow> streamHeader() {
        return Stream.of(header);
    }

    @Override
    public Stream<FooterRow> streamFooter() {
        return Stream.of(footer);
    }

    @Override
    public Stream<BodyRow> streamBodyRows() {
        return bodyRows.get();
    }
}
