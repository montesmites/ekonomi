package se.montesmites.ekonomi.nikka;

import static java.util.Map.entry;
import static se.montesmites.ekonomi.nikka.ReportType.BALANCE_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.CASHFLOW_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.RESULT_REPORT;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;

class Main {

  private static class ArgumentCaptor {

    @Argument(
        alias = "f",
        description = "destination folder for generated reports",
        required = true)
    private String destinationFolder;

    @Argument(alias = "y", description = "fiscal year for reports", required = true)
    private String year;

    @Argument(alias = "t", description = "comma separated list of report types to generate", required = true)
    private String reportTypes;

    private ArgumentCaptor(String[] args) {
      Args.parseOrExit(this, args);
    }

    private Year year() {
      return Year.parse(year);
    }

    private Supplier<Stream<ReportType>> reportTypes() {
      return () -> Arrays.stream(reportTypes.split(",")).map(ReportType::valueOf);
    }

    private Path destinationFolder() {
      return Paths.get(destinationFolder);
    }

    private Path path(ReportType reportType) {
      var pathFormat = "%s %s %s.txt";
      return destinationFolder()
          .resolve(
              String.format(
                  pathFormat,
                  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
                  REPORT_TYPE_DESCRIPTIONS.get(reportType),
                  year()));
    }
  }

  private static final Map<ReportType, String> REPORT_TYPE_DESCRIPTIONS =
      Map.ofEntries(
          entry(CASHFLOW_REPORT, "Kassaflöde"),
          entry(RESULT_REPORT, "Resultaträkning"),
          entry(BALANCE_REPORT, "Balansräkning"));

  public static void main(String[] args) {
    var arguments = new ArgumentCaptor(args);
    arguments
        .reportTypes()
        .get()
        .forEach(
            reportType -> renderReport(reportType, arguments.year(), arguments.path(reportType)));
  }

  private static void renderReport(ReportType reportType, Year year, Path path) {
    var main = new Main();
    var report = main.generateReport(reportType, year);
    main.renderToFile(report, path);
  }

  private final DataFetcher dataFetcher;

  private Main() {
    var path = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
    var organization = new OrganizationBuilder(path).build();
    this.dataFetcher = new DataFetcher(organization);
  }

  private Report generateReport(ReportType reportType, Year year) {
    return NikkaReport.of(reportType, year).generateReport(dataFetcher);
  }

  private void renderToFile(Report report, Path path) {
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, report.render());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
