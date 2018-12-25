package se.montesmites.ekonomi.report.builder;

import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;

public class SectionBuilder {

  private HeaderBuilder headerBuilder = HeaderBuilder.empty();
  private BodyBuilder bodyBuilder = BodyBuilder.empty();
  private FooterBuilder footerBuilder = FooterBuilder.empty();

  public SectionBuilder header(HeaderBuilder headerBuilder) {
    this.headerBuilder = headerBuilder;
    return this;
  }

  public SectionBuilder body(BodyBuilder bodyBuilder) {
    this.bodyBuilder = bodyBuilder;
    return this;
  }

  public SectionBuilder footer(FooterBuilder footerBuilder) {
    this.footerBuilder = footerBuilder;
    return this;
  }

  public Header getHeader() {
    return headerBuilder.header();
  }

  public Body getBody() {
    return bodyBuilder.body();
  }

  public Footer getFooter() {
    return footerBuilder.footer();
  }

  public Section section() {
    return Section.of(
        getHeader(), bodyBuilder.isMaterialized() ? getBody() : Body.empty(), getFooter());
  }
}
