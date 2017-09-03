package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public interface Section {
    
    default Stream<Row> streamTitle() {
        return Stream.empty();
    }
    
    default Stream<Row> streamHeader() {
        return Stream.empty();
    }

    default Stream<Row> streamFooter() {
        return Stream.empty();
    }
    
    default Stream<Row> streamBody() {
        return Stream.empty();
    }

    default Stream<Row> stream() {
        Stream.Builder<Row> sb = Stream.builder();
        streamTitle().forEach(sb::add);
        streamHeader().forEach(sb::add);
        streamBody().forEach(sb::add);
        streamFooter().forEach(sb::add);
        return sb.build();
    }
}
