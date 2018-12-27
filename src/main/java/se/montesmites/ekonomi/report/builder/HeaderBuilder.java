package se.montesmites.ekonomi.report.builder;

import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;

public class HeaderBuilder {

  public static HeaderBuilder empty() {
    return new HeaderBuilder();
  }

  private List<Row> rows = new ArrayList<>();

  public HeaderBuilder title(String title) {
    rows.add(Row.title(title));
    return this;
  }

  HeaderBuilder months() {
    rows.add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    return this;
  }

  public Header header() {
    return Header.of(List.copyOf(rows));
  }
}
