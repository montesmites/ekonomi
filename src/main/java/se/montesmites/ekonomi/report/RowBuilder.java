package se.montesmites.ekonomi.report;

import java.time.Year;

public class RowBuilder {
    private final CashflowDataFetcher fetcher;
    private final AccountFilter filter;
    private final java.time.Year year;
    private final String description;

    public RowBuilder(CashflowDataFetcher fetcher, AccountFilter filter, Year year, String description) {
        this.fetcher = fetcher;
        this.filter = filter;
        this.year = year;
        this.description = description;
    }

    public CashflowDataFetcher getFetcher() {
        return fetcher;
    }

    public AccountFilter getFilter() {
        return filter;
    }

    public Year getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }
}
