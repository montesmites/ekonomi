package se.montesmites.ekonomi.nikka;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.ReportBuilder;
import se.montesmites.ekonomi.report.Section;

enum NikkaSection {
  INKOMSTER(
      "Inkomster",
      List.of(
          AccountGroup.of("Löner och arvoden", "(30|36)\\d\\d"),
          AccountGroup.of("Nettoomsättning övrigt", "3([1-5]|[7-9])\\d\\d"))),
  BOENDE(
      "Boende",
      List.of(
          AccountGroup.of("Månadsavgift m.m.", "501\\d"),
          AccountGroup.of("Amortering Fredsgatan 13", "2353"),
          AccountGroup.of("Bolån ränta och liknande", "84[019]\\d"),
          AccountGroup.of("Hemförsäkring", "5081"),
          AccountGroup.of("El (förbrukning och nät)", "502\\d"),
          AccountGroup.of("Mobil, tv, bredband", "51\\d\\d"))),
  FORNODENHETER(
      "Förnödenheter",
      List.of(
          AccountGroup.of("Dagligvaror", "40\\d\\d"),
          AccountGroup.of("Kläder och skor", "41\\d\\d"),
          AccountGroup.of("Kropp och själ", "42\\d\\d"),
          AccountGroup.of("Personförsäkringar", "43\\d\\d"),
          AccountGroup.of("A-kassa, fack, bank, skatt", "4[4-7]\\d\\d"),
          AccountGroup.of("Transporter", "56\\d\\d"))),
  OVRIGT(
      "Övrigt",
      List.of(
          AccountGroup.of("Boende diverse", "5060|5[458]\\d\\d"),
          AccountGroup.of("Övrigt", "(4[89]|[67]\\d)\\d\\d"))),
  JAMFORELSESTORANDE_POSTER(
      "Jämförelsestörande poster",
      List.of(
          AccountGroup.of("Kortfristigt netto", "(1[5-8]|2[4-9])\\d\\d"),
          AccountGroup.of("Långfristigt netto", "(8[56]\\d\\d)|(10\\d\\d|13[456]\\d)"),
          AccountGroup.of("Finansiellt netto", "(83\\d\\d)|(84[2-8]\\d)"),
          AccountGroup.of("Investering boende", "11\\d\\d"),
          AccountGroup.of("Extraordinärt netto", "87\\d\\d"))),
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
