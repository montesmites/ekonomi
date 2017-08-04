package se.montesmites.ekonomi.report;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public class Section {

    private final CashflowDataFetcher fetcher;
    private final java.time.Year year;
    private final TitleRow title;
    private final HeaderRow header;
    private final FooterRow footer;

    public Section(String title, CashflowDataFetcher fetcher, java.time.Year year) {
        this.fetcher = fetcher;
        this.year = year;
        this.title = new TitleRow(title);
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
                .map(accountId
                        -> new BodyRow(
                        fetcher,
                        set(accountId),
                        year,
                        accountId.getId()));
    }

    private Set<AccountId> set(AccountId accountId) {
        Set<AccountId> set = new HashSet<>();
        set.add(accountId);
        return set;
    }

    public Stream<Row> streamAllRows() {
        Stream.Builder<Row> sb = Stream.builder();
        sb.add(title);
        sb.add(header);
        streamBodyRows().forEach(sb::add);
        sb.add(footer);
        return sb.build();
    }
}
