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
    public TitleRow getTitle() {
        return this.title;
    }

    @Override
    public HeaderRow getHeader() {
        return header;
    }

    @Override
    public FooterRow getFooter() {
        return footer;
    }

    @Override
    public Stream<BodyRow> streamBodyRows() {
        return bodyRows.get();
    }
}
