package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public class Section {

    private final CashflowDataFetcher fetcher;
    private final java.time.Year year;
    private HeaderRow header;

    public Section(CashflowDataFetcher fetcher, java.time.Year year) {
        this.fetcher = fetcher;
        this.year = year;
    }

    public HeaderRow getHeader() {
        return header;
    }

    public Stream<BodyRow> bodyStream() {
        return fetcher.streamAccountIds(year)
                .map(accountId -> new BodyRow(fetcher, accountId, year));
    }
}
