package se.montesmites.ekonomi.nikka;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.nikka.NikkaSection.BOENDE;
import static se.montesmites.ekonomi.nikka.NikkaSection.FORANDRING_LIKVIDA_MEDEL;
import static se.montesmites.ekonomi.nikka.NikkaSection.FORNODENHETER;
import static se.montesmites.ekonomi.nikka.NikkaSection.INKOMSTER;
import static se.montesmites.ekonomi.nikka.NikkaSection.JAMFORELSESTORANDE_POSTER;
import static se.montesmites.ekonomi.nikka.NikkaSection.OVRIGT;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccumulatingNegatedRow;
import se.montesmites.ekonomi.report.AccumulatingSection;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.CompactSectionDecorator;
import se.montesmites.ekonomi.report.DefaultRowWithAccountsWithNegatedAmounts;
import se.montesmites.ekonomi.report.RowWithAmounts;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.TotallingSection;

class Main {

  public static void main(String[] args) throws Exception {
    var year = Year.of(2018);
    var fmt = "c:/temp/nikka/Kassaflöde %d.txt";
    var fileName = String.format(fmt, year.getValue());
    var path = Paths.get(fileName);
    var main = new Main();
    var report = main.generateCashflowReport(year);
    main.renderToFile(report, path);
  }

  private final CashflowDataFetcher fetcher;

  private Main() {
    var path = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
    var organization = new OrganizationBuilder(path).build();
    this.fetcher = new CashflowDataFetcher(organization);
  }

  private CashflowReport generateCashflowReport(Year year) {
    var foreJamforelsestorandePoster =
        new TotallingSection(
            "Före jämförelsestörande poster",
            sections(year, INKOMSTER, BOENDE, FORNODENHETER, OVRIGT));
    var total =
        new TotallingSection(
            "Kontrollsumma",
            sections(
                year,
                INKOMSTER,
                BOENDE,
                FORNODENHETER,
                OVRIGT,
                JAMFORELSESTORANDE_POSTER,
                FORANDRING_LIKVIDA_MEDEL)) {
          @Override
          public RowWithAmounts wrapSectionRow(Section section, RowWithAmounts row) {
            if (sectionEqualsForandringLikvidaMedel(section)) {
              return new DefaultRowWithAccountsWithNegatedAmounts(
                  row.asRowWithAccounts().orElseThrow());
            } else {
              return row;
            }
          }

          private boolean sectionEqualsForandringLikvidaMedel(Section section) {
            return section
                .header()
                .stream()
                .findFirst()
                .orElseThrow()
                .format(DESCRIPTION)
                .trim()
                .toUpperCase()
                .equals(NikkaSection.FORANDRING_LIKVIDA_MEDEL.getTitle().trim().toUpperCase());
          }
        };
    var accumulation =
        AccumulatingSection.of(
            "Ackumulerade likvida medel",
            () ->
                Stream.of(
                    new AccumulatingNegatedRow(
                        fetcher,
                        () ->
                            new AccountFilterByRegex("1493|19\\d\\d")
                                .filter(fetcher.streamAccountIds(year)),
                        year)));
    return new CashflowReport(
        fetcher,
        year,
        () ->
            Stream.of(
                s(year, INKOMSTER),
                s(year, BOENDE),
                s(year, FORNODENHETER),
                s(year, OVRIGT),
                new CompactSectionDecorator().decorate(foreJamforelsestorandePoster),
                s(year, JAMFORELSESTORANDE_POSTER),
                new CompactSectionDecorator().decorate(s(year, FORANDRING_LIKVIDA_MEDEL)),
                new CompactSectionDecorator().decorate(total),
                accumulation));
  }

  private void renderToFile(CashflowReport report, Path path) throws IOException {
    Files.write(path, report.render());
  }

  private List<Section> sections(Year year, NikkaSection... sections) {
    return Arrays.stream(sections).map(section -> section.section(fetcher, year)).collect(toList());
  }

  private Section s(Year year, NikkaSection section) {
    return section.section(fetcher, year);
  }
}
