package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Section {

    private final TitleRow title;
    private final HeaderRow header;
    private final FooterRow footer;
    private final Supplier<Stream<BodyRow>> bodyRows;

    public Section(String title, java.time.Year year, Supplier<Stream<BodyRow>> bodyRows) {
        this.title = new TitleRow(title);
        this.header = new HeaderRow();
        this.footer = new FooterRow(this, year);
        this.bodyRows = bodyRows;
    }
    
    public TitleRow getTitle() {
        return this.title;
    }
    
    public HeaderRow getHeader() {
        return header;
    }

    public FooterRow getFooter() {
        return footer;
    }

    public Stream<BodyRow> streamBodyRows() {
        return bodyRows.get();
    }

    public Stream<Row> streamAllRows() {
        Stream.Builder<Row> sb = Stream.builder();
        sb.add(title);
        sb.add(header);
        streamBodyRows().forEach(sb::add);
        sb.add(footer);
        sb.add(new EmptyRow());
        return sb.build();
    }
}
