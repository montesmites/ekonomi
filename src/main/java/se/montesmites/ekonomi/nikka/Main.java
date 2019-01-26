package se.montesmites.ekonomi.nikka;

import static java.util.Map.entry;
import static se.montesmites.ekonomi.nikka.ReportType.BALANCE_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.CASHFLOW_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.RESULT_REPORT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;

class Main {

  private static final Map<ReportType, String> REPORT_TYPE_DESCRIPTIONS =
      Map.ofEntries(
          entry(CASHFLOW_REPORT, "Kassaflöde"),
          entry(RESULT_REPORT, "Resultaträkning"),
          entry(BALANCE_REPORT, "Balansräkning"));

  public static void main(String[] args) {
    var year = Year.of(2018);
    var destinationFolder = Paths.get("c:/temp/nikka/");
    var pathFormat = "%s %s %d.txt";
    Stream.of(CASHFLOW_REPORT, RESULT_REPORT, BALANCE_REPORT)
        .forEach(
            type ->
                renderReport(
                    type,
                    year,
                    destinationFolder.resolve(
                        String.format(
                            pathFormat,
                            LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
                            REPORT_TYPE_DESCRIPTIONS.get(type),
                            year.getValue()))));
  }

  private static void renderReport(ReportType type, Year year, Path path) {
    var main = new Main();
    var report = main.generateReport(type, year);
    main.renderToFile(report, path);
  }

  private final DataFetcher dataFetcher;

  private Main() {
    var path = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
    var organization = new OrganizationBuilder(path).build();
    this.dataFetcher = new DataFetcher(organization);
  }

  private Report generateReport(ReportType type, Year year) {
    return NikkaReport.of(type, year).generateReport(dataFetcher);
  }

  private void renderToFile(Report report, Path path) {
    try {
      Files.write(path, report.render());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
