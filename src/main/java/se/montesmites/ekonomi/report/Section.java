package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public class Section {

    private final CashflowDataFetcher fetcher;
    private final java.time.Year year;
    private final HeaderRow header;
    private final FooterRow footer;

    public Section(CashflowDataFetcher fetcher, java.time.Year year) {
        this.fetcher = fetcher;
        this.year = year;
        this.header = new HeaderRow();
        this.footer = new FooterRow(this, year);
    }

    public HeaderRow getHeader() {
        return header;
    }
    
    public FooterRow getFooter() {
        return footer;
    }
    
    public Stream<BodyRow> streamBodyRows() {
        return fetcher.streamAccountIds(year)
                .map(accountId -> new BodyRow(fetcher, accountId, year));
    }
}
