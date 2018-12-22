package se.montesmites.ekonomi.report.builder;

import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;

public class SectionBuilder {

  public static HeaderBuilder headerBuilder() {
    return new HeaderBuilder();
  }

  private HeaderBuilder headerBuilder = new HeaderBuilder();
  private Body body = Body.empty();
  private Footer footer = Footer.empty();

  public SectionBuilder header(HeaderBuilder headerBuilder) {
    this.headerBuilder = headerBuilder;
    return this;
  }

  public SectionBuilder body(Body body) {
    this.body = body;
    return this;
  }

  public SectionBuilder footer(Footer footer) {
    this.footer = footer;
    return this;
  }

  public Header getHeader() {
    return headerBuilder.header();
  }

  public Body getBody() {
    return body;
  }

  public Footer getFooter() {
    return footer;
  }

  public Section section() {
    return Section.of(getHeader(), body, footer);
  }
}
