package se.montesmites.ekonomi.nikka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Set;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.Tag;
import se.montesmites.ekonomi.report.TagFilter;
import se.montesmites.ekonomi.report.builder.ReportBuilder;

class Main {

  private static final String LIKVIDA_MEDEL_REGEX = "1493|19\\d\\d";

  public static void main(String[] args) throws Exception {
    var year = Year.of(2018);
    cashflowReport(year);
    resultReport(year);
  }

  private static void cashflowReport(Year year) throws Exception {
    var fmt = "c:/temp/nikka/Kassaflöde %d.txt";
    var fileName = String.format(fmt, year.getValue());
    var path = Paths.get(fileName);
    var main = new Main();
    var report = main.generateCashflowReport(year);
    main.renderToFile(report, path);
  }

  private static void resultReport(Year year) throws Exception {
    var fmt = "c:/temp/nikka/Resultaträkning %d.txt";
    var fileName = String.format(fmt, year.getValue());
    var path = Paths.get(fileName);
    var main = new Main();
    var report = main.generateResultReport(year);
    main.renderToFile(report, path);
  }

  private final DataFetcher dataFetcher;

  private Main() {
    var path = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
    var organization = new OrganizationBuilder(path).build();
    this.dataFetcher = new DataFetcher(organization);
  }

  private Report generateCashflowReport(Year year) {
    return new ReportBuilder(dataFetcher, year)
        .accountGroups(
            "Inkomster",
            List.of(
                AccountGroup.of("Löner och arvoden", "(30|36)\\d\\d"),
                AccountGroup.of("Nettoomsättning övrigt", "3([1-5]|[7-9])\\d\\d")))
        .accountGroups(
            "Boende",
            List.of(
                AccountGroup.of("Månadsavgift m.m.", "501\\d"),
                AccountGroup.of("Amortering Fredsgatan 13", "2353"),
                AccountGroup.of("Bolån ränta och liknande", "84[019]\\d"),
                AccountGroup.of("Hemförsäkring", "5081"),
                AccountGroup.of("El (förbrukning och nät)", "502\\d"),
                AccountGroup.of("Mobil, tv, bredband", "51\\d\\d")))
        .accountGroups(
            "Förnödenheter",
            List.of(
                AccountGroup.of("Dagligvaror", "40\\d\\d"),
                AccountGroup.of("Kläder och skor", "41\\d\\d"),
                AccountGroup.of("Kropp och själ", "42\\d\\d"),
                AccountGroup.of("Personförsäkringar", "43\\d\\d"),
                AccountGroup.of("A-kassa, fack, bank, skatt", "4[4-7]\\d\\d"),
                AccountGroup.of("Transporter", "56\\d\\d")))
        .accountGroups(
            "Övrigt",
            List.of(
                AccountGroup.of("Boende diverse", "5060|5[458]\\d\\d"),
                AccountGroup.of("Övrigt", "(4[89]|[67]\\d)\\d\\d")))
        .subtotal("Före jämförelsestörande poster".toUpperCase(), TagFilter.any())
        .accountGroups(
            "Jämförelsestörande poster",
            List.of(
                AccountGroup.of("Kortfristigt netto", "(1[5-8]|2[4-9])\\d\\d"),
                AccountGroup.of("Långfristigt netto", "(8[56]\\d\\d)|(10\\d\\d|13[456]\\d)"),
                AccountGroup.of("Finansiellt netto", "(83\\d\\d)|(84[2-8]\\d)"),
                AccountGroup.of("Investering boende", "11\\d\\d"),
                AccountGroup.of("Extraordinärt netto", "87\\d\\d")))
        .subtotal("Förändring likvida medel".toUpperCase(), TagFilter.any())
        .section(
            section ->
                section
                    .body(
                        body ->
                            body.accountGroups(
                                List.of(AccountGroup.of("Likvida medel", LIKVIDA_MEDEL_REGEX)))
                                .dematerialize())
                    .noClosingEmptyRow())
        .subtotal("Kontrollsumma".toUpperCase(), TagFilter.any())
        .accumulateAccountGroups(
            "Ackumulerade likvida medel",
            List.of(
                AccountGroup.of("", LIKVIDA_MEDEL_REGEX).postProcessor(AmountsProvider::negate)))
        .report();
  }

  private Report generateResultReport(Year year) {
    return new ReportBuilder(dataFetcher, dataFetcher, year)
        .tags(Set.of(Tag.of("Bruttoresultat")))
        .accounts("Intäkter", "3\\d\\d\\d", AmountsProvider::self)
        .accounts("Förnödenheter", "4\\d\\d\\d", AmountsProvider::self)
        .accounts("Boende", "50\\d\\d", AmountsProvider::self)
        .accounts("Övriga kostnader", "(5[1-9]|[67]\\d)\\d\\d", AmountsProvider::self)
        .accounts("Finansiellt netto", "8[3456]\\d\\d", AmountsProvider::self)
        .subtotal("Bruttoresultat".toUpperCase(), TagFilter.isEqualTo(Tag.of("Bruttoresultat")))
        .accounts("Extraordinärt netto", "87\\d\\d", AmountsProvider::self)
        .subtotal("Beräknat resultat".toUpperCase(), TagFilter.any())
        .section(
            section ->
                section
                    .body(
                        body ->
                            body.accountGroups(
                                List.of(
                                    AccountGroup.of("Kontrollsumma", "([3-7]\\d|8[1-7])\\d\\d")
                                        .postProcessor(AmountsProvider::negate)))
                                .dematerialize())
                    .noClosingEmptyRow())
        .subtotal("Kontrollsumma".toUpperCase(), TagFilter.any())
        .accumulateAccountGroups(
            "Ackumulerat resultat",
            List.of(AccountGroup.of("Kontrollsumma", "([3-7]\\d|8[1-7])\\d\\d")))
        .report();
  }

  private void renderToFile(Report report, Path path) throws IOException {
    Files.write(path, report.render());
  }
}
