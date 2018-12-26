package se.montesmites.ekonomi.report.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Row;

public class FooterBuilder {

  public static FooterBuilder empty() {
    return new FooterBuilder(null) {
      @Override
      FooterBuilder aggregateBody() {
        return this;
      }

      @Override
      public FooterBuilder accumulateBody(Currency initialBalance) {
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

  FooterBuilder aggregateBody() {
    return aggregateBody("");
  }

  private FooterBuilder aggregateBody(String description) {
    this.rows.add(this.body.get().aggregate(description).asRow());
    return this;
  }

  FooterBuilder accumulateBody(Currency initalBalance) {
    var aggregate = this.body.get().aggregate("");
    var accumulation = aggregate.accumulate(initalBalance);
    this.rows.add(accumulation.asRow());
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
