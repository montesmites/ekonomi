package se.montesmites.ekonomi.nikka;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.builder.ReportBuilder;

enum NikkaSection {
  FORANDRING_LIKVIDA_MEDEL(
      "Förändring likvida medel", List.of(AccountGroup.of("Likvida medel", "1493|19\\d\\d"))) {
    @Override
    protected UnaryOperator<AmountsProvider> getPostProcessor() {
      return AmountsProvider::negate;
    }
  };

  private final String title;
  private final List<AccountGroup> groups;

  NikkaSection(String title, List<AccountGroup> groups) {
    this.title = title;
    this.groups = groups;
  }

  public String getTitle() {
    return title;
  }

  public List<AccountGroup> getGroups() {
    return groups.stream().map(group -> group.postProcessor(getPostProcessor())).collect(toList());
  }

  protected UnaryOperator<AmountsProvider> getPostProcessor() {
    return row -> row;
  }

  Section toSection(ReportBuilder reportBuilder) {
    return reportBuilder.buildSection(this.getTitle(), this.getGroups());
  }
}
