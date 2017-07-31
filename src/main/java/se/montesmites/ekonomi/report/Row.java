package se.montesmites.ekonomi.report;

import java.util.Arrays;
import java.util.stream.Stream;

public class Row {

    public Stream<Column> columnStream() {
        return Arrays.asList("Description", "Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Total").stream().map(Column::new);
    }
}
