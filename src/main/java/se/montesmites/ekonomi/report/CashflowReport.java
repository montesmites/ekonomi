package se.montesmites.ekonomi.report;

import java.util.Arrays;
import java.util.stream.Stream;

public class CashflowReport {

    private final CashflowDataFetcher fetcher;
    private final java.time.Year year;

    public CashflowReport(CashflowDataFetcher fetcher, java.time.Year year) {
        this.fetcher = fetcher;
        this.year = year;
    }
    
    public Stream<Column> streamColumns() {
        return Arrays.asList("Description", "Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Total").stream().map(Column::new);
    }

    public Stream<Section> streamSections() {
        return Stream.of(new Section(fetcher, year));
    }
}
