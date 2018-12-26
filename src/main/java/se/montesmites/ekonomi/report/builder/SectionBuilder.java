package se.montesmites.ekonomi.report.builder;

import java.time.Year;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;

public class SectionBuilder {

  private final HeaderBuilder headerBuilder;
  private final BodyBuilder bodyBuilder;
  private final FooterBuilder footerBuilder;

  SectionBuilder(Year year, AmountFetcher amountFetcher) {
    this.headerBuilder = new HeaderBuilder();
    this.bodyBuilder = new BodyBuilder(year, amountFetcher);
    this.footerBuilder = new FooterBuilder(this.bodyBuilder::body);
  }

  public SectionBuilder header(UnaryOperator<HeaderBuilder> header) {
    header.apply(this.headerBuilder);
    return this;
  }

  public SectionBuilder body(UnaryOperator<BodyBuilder> body) {
    body.apply(this.bodyBuilder);
    return this;
  }

  public SectionBuilder footer(UnaryOperator<FooterBuilder> footer) {
    footer.apply(this.footerBuilder);
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
