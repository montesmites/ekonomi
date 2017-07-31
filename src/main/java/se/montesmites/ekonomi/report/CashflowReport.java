package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

public class CashflowReport {

    private final AccountAmountFetcher fetcher;

    public CashflowReport(AccountAmountFetcher fetcher) {
        this.fetcher = fetcher;
    }
    
    public Stream<Section> sectionStream() {
        return Stream.of(new Section());
    }
    
}
