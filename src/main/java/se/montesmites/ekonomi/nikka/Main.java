package se.montesmites.ekonomi.nikka;

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
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.ReportBuilder;

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
    var reportBuilder = new ReportBuilder(fetcher, year);
    var inkomster = INKOMSTER.toSection(reportBuilder);
    var boende = BOENDE.toSection(reportBuilder);
    var fornodenheter = FORNODENHETER.toSection(reportBuilder);
    var ovrigt = OVRIGT.toSection(reportBuilder);
    var foreJamforelsestorandePoster =
        reportBuilder.buildSection(
            inkomster
                .body()
                .concat(boende.body())
                .concat(fornodenheter.body())
                .concat(ovrigt.body())
                .aggregate("Före jämförelsestörande poster".toUpperCase())
                .asRow());
    var jamforelsestorandePoster = JAMFORELSESTORANDE_POSTER.toSection(reportBuilder);
    var forandringLikvidaMedel =
        reportBuilder.buildSection(
            FORANDRING_LIKVIDA_MEDEL
                .toSection(reportBuilder)
                .body()
                .aggregate(FORANDRING_LIKVIDA_MEDEL.getTitle().toUpperCase())
                .asRow());
    var kontrollsumma =
        reportBuilder.buildSection(
            inkomster
                .body()
                .concat(boende.body())
                .concat(fornodenheter.body())
                .concat(ovrigt.body())
                .concat(jamforelsestorandePoster.body())
                .concat(FORANDRING_LIKVIDA_MEDEL.toSection(reportBuilder).body().negate())
                .aggregate("Kontrollsumma".toUpperCase())
                .asRow());
    var accumulation =
        reportBuilder.buildSectionWithAcculumatingFooter(
            "Ackumulerade likvida medel",
            AccountGroup.of("", "1493|19\\d\\d").postProcessor(AmountsProvider::negate));
    return new CashflowReport(
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
}
