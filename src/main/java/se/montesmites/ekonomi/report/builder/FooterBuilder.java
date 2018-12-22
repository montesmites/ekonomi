package se.montesmites.ekonomi.report.builder;

import se.montesmites.ekonomi.report.Footer;

public class FooterBuilder {

  public static FooterBuilder empty() {
    return new FooterBuilder();
  }

  public Footer footer() {
    return Footer.empty();
  }
}
