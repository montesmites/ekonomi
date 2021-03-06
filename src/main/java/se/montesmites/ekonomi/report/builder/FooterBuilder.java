package se.montesmites.ekonomi.report.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import se.montesmites.ekonomi.report.Aggregate;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Row;

public class FooterBuilder {

  public static FooterBuilder empty() {
    return new FooterBuilder(null) {
      @Override
      public FooterBuilder aggregateBody() {
        return this;
      }

      @Override
      public Footer footer() {
        return Footer.empty();
      }
    };
  }

  private final Supplier<Body> body;
  private List<Row> rows = new ArrayList<>();

  FooterBuilder(Supplier<Body> body) {
    this.body = body;
  }

  public FooterBuilder aggregateBody() {
    return aggregateBody("");
  }

  private FooterBuilder aggregateBody(String description) {
    this.rows.add(Aggregate.of(description, body.get()).asRow());
    return this;
  }

  FooterBuilder add(Row row) {
    this.rows.add(row);
    return this;
  }

  public Footer footer() {
    return Footer.of(rows);
  }
}
