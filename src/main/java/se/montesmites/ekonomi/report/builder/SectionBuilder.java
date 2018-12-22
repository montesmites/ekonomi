package se.montesmites.ekonomi.report.builder;

import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;

public class SectionBuilder {

  private HeaderBuilder headerBuilder = HeaderBuilder.empty();
  private BodyBuilder bodyBuilder = BodyBuilder.empty();
  private Footer footer = Footer.empty();

  public SectionBuilder header(HeaderBuilder headerBuilder) {
    this.headerBuilder = headerBuilder;
    return this;
  }

  public SectionBuilder body(BodyBuilder bodyBuilder) {
    this.bodyBuilder = bodyBuilder;
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
    return bodyBuilder.body();
  }

  public Footer getFooter() {
    return footer;
  }

  public Section section() {
    return Section.of(
        getHeader(), bodyBuilder.bodyIsTransient() ? Body.empty() : getBody(), footer);
  }
}
