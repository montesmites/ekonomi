package se.montesmites.ekonomi.report.builder;

import java.util.stream.Stream;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;

public class BodyBuilder {

  private Stream.Builder<AmountsProvider> amountsProviders = Stream.builder();

  public Body body() {
    return Body.of(() -> amountsProviders.build());
  }
}
