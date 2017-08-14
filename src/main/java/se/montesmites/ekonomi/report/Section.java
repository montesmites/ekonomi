package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Section {

    public TitleRow getTitle();
    
    public HeaderRow getHeader();

    public FooterRow getFooter();
    
    public Stream<BodyRow> streamBodyRows();

    default Stream<Row> streamAllRows() {
        Stream.Builder<Row> sb = Stream.builder();
        sb.add(getTitle());
        sb.add(getHeader());
        streamBodyRows().forEach(sb::add);
        sb.add(getFooter());
        sb.add(new EmptyRow());
        return sb.build();
    }
}
