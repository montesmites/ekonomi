package se.montesmites.ekonomi.report.builder;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.Tag;

public class SectionBuilder {

  private final HeaderBuilder headerBuilder;
  private final BodyBuilder bodyBuilder;
  private final FooterBuilder footerBuilder;
  private Set<Tag> tags = new HashSet<>();
  private boolean closingEmptyRow = true;

  SectionBuilder(Year year, AmountsFetcher amountsFetcher) {
    this.headerBuilder = new HeaderBuilder();
    this.bodyBuilder = new BodyBuilder(year, amountsFetcher);
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

  SectionBuilder tag(Tag tag) {
    this.tags.add(tag);
    return this;
  }

  public SectionBuilder noClosingEmptyRow() {
    this.closingEmptyRow = false;
    return this;
  }

  Set<Tag> getTags() {
    return Set.copyOf(this.tags);
  }

  boolean hasClosingEmptyRow() {
    return this.closingEmptyRow;
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

  BodyBuilder getBodyBuilder() {
    return bodyBuilder;
  }

  public Section section() {
    return Section.of(
        getHeader(), bodyBuilder.isMaterialized() ? getBody() : Body.empty(), getFooter())
        .closingEmptyRow(closingEmptyRow)
        .tags(tags);
  }
}
