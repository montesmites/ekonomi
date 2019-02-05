package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.Aggregate;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.TagFilter;

public class SubtotalBuilder {

  private final List<SectionBuilder> sections;
  private String description = "";
  private TagFilter tagFilter = TagFilter.any();

  SubtotalBuilder(List<SectionBuilder> sections) {
    this.sections = sections;
  }

  public SubtotalBuilder description(String description) {
    this.description = description;
    return this;
  }

  public SubtotalBuilder tagFilter(TagFilter tagFilter) {
    this.tagFilter = tagFilter;
    return this;
  }

  public SubtotalBuilder section(SectionBuilder sectionBuilder) {
    var aggregates =
        List.copyOf(this.sections)
            .stream()
            .filter(section -> tagFilter.test(section.getTags()))
            .map(SectionBuilder::getBodyBuilder)
            .map(BodyBuilder::body)
            .map(Aggregate::of)
            .collect(toList());
    sectionBuilder.footer(
        footer ->
            footer.add(
                AmountsProvider.of(
                    this.description,
                    month ->
                        aggregates
                            .stream()
                            .map(amountsProvider -> amountsProvider.getMonthlyAmount(month))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .reduce(Currency::add))
                    .asRow()));
    return this;
  }
}
