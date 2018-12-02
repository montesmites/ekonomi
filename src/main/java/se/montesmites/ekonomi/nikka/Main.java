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
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.ReportBuilder;
import se.montesmites.ekonomi.report.RowWithAmounts;

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
                .aggregate()
                .description("Före jämförelsestörande poster".toUpperCase()));
    var jamforelsestorandePoster = JAMFORELSESTORANDE_POSTER.toSection(reportBuilder);
    var forandringLikvidaMedel =
        reportBuilder.buildSection(
            FORANDRING_LIKVIDA_MEDEL
                .toSection(reportBuilder)
                .body()
                .aggregate()
                .description(FORANDRING_LIKVIDA_MEDEL.getTitle().toUpperCase()));
    var kontrollsumma =
        reportBuilder.buildSection(
            inkomster
                .body()
                .concat(boende.body())
                .concat(fornodenheter.body())
                .concat(ovrigt.body())
                .concat(jamforelsestorandePoster.body())
                .concat(FORANDRING_LIKVIDA_MEDEL.toSection(reportBuilder).body().negate())
                .aggregate()
                .description("Kontrollsumma".toUpperCase()));
    var liquidFundsAccountsRegex = "1493|19\\d\\d";
    var accumulation =
        reportBuilder.buildSection(
            "Ackumulerade likvida medel",
            fetcher
                .reportBuilderOf(year)
                .buildRowWithAmounts(
                    AccountGroup.of("", liquidFundsAccountsRegex)
                        .postProcessor(RowWithAmounts::negate))
                .accumulate(balance(year, AccountFilterByRegex.of(liquidFundsAccountsRegex))));
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

  private Currency balance(Year year, Predicate<AccountId> filter) {
    return fetcher
        .streamAccountIds(year, filter)
        .map(this::balance)
        .reduce(new Currency(0), Currency::add);
  }

  private Currency balance(AccountId accountId) {
    return fetcher.fetchBalance(accountId).map(Balance::getBalance).orElse(new Currency(0));
  }
}
