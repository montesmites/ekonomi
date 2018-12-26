package se.montesmites.ekonomi.nikka;

import static se.montesmites.ekonomi.nikka.NikkaSection.FORANDRING_LIKVIDA_MEDEL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.builder.ReportBuilder;

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
    var inkomster =
        reportBuilder.buildSection(
            "Inkomster",
            List.of(
                AccountGroup.of("Löner och arvoden", "(30|36)\\d\\d"),
                AccountGroup.of("Nettoomsättning övrigt", "3([1-5]|[7-9])\\d\\d")));
    var boende =
        reportBuilder.buildSection(
            "Boende",
            List.of(
                AccountGroup.of("Månadsavgift m.m.", "501\\d"),
                AccountGroup.of("Amortering Fredsgatan 13", "2353"),
                AccountGroup.of("Bolån ränta och liknande", "84[019]\\d"),
                AccountGroup.of("Hemförsäkring", "5081"),
                AccountGroup.of("El (förbrukning och nät)", "502\\d"),
                AccountGroup.of("Mobil, tv, bredband", "51\\d\\d")));
    var fornodenheter =
        reportBuilder.buildSection(
            "Förnödenheter",
            List.of(
                AccountGroup.of("Dagligvaror", "40\\d\\d"),
                AccountGroup.of("Kläder och skor", "41\\d\\d"),
                AccountGroup.of("Kropp och själ", "42\\d\\d"),
                AccountGroup.of("Personförsäkringar", "43\\d\\d"),
                AccountGroup.of("A-kassa, fack, bank, skatt", "4[4-7]\\d\\d"),
                AccountGroup.of("Transporter", "56\\d\\d")));
    var ovrigt =
        reportBuilder.buildSection(
            "Övrigt",
            List.of(
                AccountGroup.of("Boende diverse", "5060|5[458]\\d\\d"),
                AccountGroup.of("Övrigt", "(4[89]|[67]\\d)\\d\\d")));
    var foreJamforelsestorandePoster =
        Section.of(
            Header.empty(),
            Body.empty(),
            Footer.of(
                inkomster
                    .body()
                    .concat(boende.body())
                    .concat(fornodenheter.body())
                    .concat(ovrigt.body())
                    .aggregate("Före jämförelsestörande poster".toUpperCase())
                    .asRow()));
    var jamforelsestorandePoster =
        reportBuilder.buildSection(
            "Jämförelsestörande poster",
            List.of(
                AccountGroup.of("Kortfristigt netto", "(1[5-8]|2[4-9])\\d\\d"),
                AccountGroup.of("Långfristigt netto", "(8[56]\\d\\d)|(10\\d\\d|13[456]\\d)"),
                AccountGroup.of("Finansiellt netto", "(83\\d\\d)|(84[2-8]\\d)"),
                AccountGroup.of("Investering boende", "11\\d\\d"),
                AccountGroup.of("Extraordinärt netto", "87\\d\\d")));
    var forandringLikvidaMedel =
        Section.of(
            Header.empty(),
            Body.empty(),
            Footer.of(
                FORANDRING_LIKVIDA_MEDEL
                    .toSection(reportBuilder)
                    .body()
                    .aggregate(FORANDRING_LIKVIDA_MEDEL.getTitle().toUpperCase())
                    .asRow()));
    var kontrollsumma =
        Section.of(
            Header.empty(),
            Body.empty(),
            Footer.of(
                inkomster
                    .body()
                    .concat(boende.body())
                    .concat(fornodenheter.body())
                    .concat(ovrigt.body())
                    .concat(jamforelsestorandePoster.body())
                    .concat(FORANDRING_LIKVIDA_MEDEL.toSection(reportBuilder).body().negate())
                    .aggregate("Kontrollsumma".toUpperCase())
                    .asRow()));
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
