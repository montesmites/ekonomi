package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Section {
    
    public Stream<TitleRow> streamTitle();
    
    public Stream<HeaderRow> streamHeader();

    public Stream<FooterRow> streamFooter();
    
    public Stream<BodyRow> streamBodyRows();

    default Stream<Row> streamAllRows() {
        Stream.Builder<Row> sb = Stream.builder();
        streamTitle().forEach(sb::add);
        streamHeader().forEach(sb::add);
        streamBodyRows().forEach(sb::add);
        streamFooter().forEach(sb::add);
        sb.add(new EmptyRow());
        return sb.build();
    }
}
