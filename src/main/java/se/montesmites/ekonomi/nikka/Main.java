package se.montesmites.ekonomi.nikka;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.nikka.NikkaSection.BOENDE;
import static se.montesmites.ekonomi.nikka.NikkaSection.FORANDRING_LIKVIDA_MEDEL;
import static se.montesmites.ekonomi.nikka.NikkaSection.FORNODENHETER;
import static se.montesmites.ekonomi.nikka.NikkaSection.INKOMSTER;
import static se.montesmites.ekonomi.nikka.NikkaSection.JAMFORELSESTORANDE_POSTER;
import static se.montesmites.ekonomi.nikka.NikkaSection.OVRIGT;

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
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

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
    var inkomster = s(year, INKOMSTER);
    var boende = s(year, BOENDE);
    var fornodenheter = s(year, FORNODENHETER);
    var ovrigt = s(year, OVRIGT);
    var foreJamforelsestorandePoster =
        s(
            inkomster
                .body()
                .concat(boende.body())
                .concat(fornodenheter.body())
                .concat(ovrigt.body())
                .aggregate()
                .description("Före jämförelsestörande poster"));
    var jamforelsestorandePoster = s(year, JAMFORELSESTORANDE_POSTER);
    var forandringLikvidaMedel =
        s(
            s(year, FORANDRING_LIKVIDA_MEDEL)
                .body()
                .aggregate()
                .description(FORANDRING_LIKVIDA_MEDEL.getTitle()));
    var kontrollsumma =
        s(
            inkomster
                .body()
                .concat(boende.body())
                .concat(fornodenheter.body())
                .concat(ovrigt.body())
                .concat(jamforelsestorandePoster.body())
                .concat(forandringLikvidaMedel.body().negate())
                .aggregate()
                .description("Kontrollsumma"));
    return new CashflowReport(
        fetcher,
        year,
        () ->
            Stream.of(
                inkomster,
                boende,
                fornodenheter,
                ovrigt,
                foreJamforelsestorandePoster,
                jamforelsestorandePoster,
                forandringLikvidaMedel,
                kontrollsumma,
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

  private Section s(Row row) {
    return Section.of(Header.empty(), Body.empty(), Footer.of(row));
  }
}
