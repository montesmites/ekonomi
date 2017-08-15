package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Section {
    
    public Stream<Row> streamTitle();
    
    public Stream<Row> streamHeader();

    public Stream<Row> streamFooter();
    
    public Stream<Row> streamBodyRows();

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
