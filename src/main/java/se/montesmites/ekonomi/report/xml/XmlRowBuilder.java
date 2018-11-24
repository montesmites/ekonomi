package se.montesmites.ekonomi.report.xml;

import java.time.Year;
import se.montesmites.ekonomi.report.AccountFilter;
import se.montesmites.ekonomi.report.CashflowDataFetcher;

public class XmlRowBuilder {

  private final CashflowDataFetcher fetcher;
  private final AccountFilter filter;
  private final java.time.Year year;
  private final String description;

  XmlRowBuilder(CashflowDataFetcher fetcher, AccountFilter filter, Year year, String description) {
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
