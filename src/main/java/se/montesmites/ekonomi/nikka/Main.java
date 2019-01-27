package se.montesmites.ekonomi.nikka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;

class Main {
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
    try (var writer = Files.newBufferedWriter(path)) {
      Files.createDirectories(path.getParent());
      var lines = report.renderWithNoTrailingEmptyRows();
      for (var i = 0; i < lines.size() - 1; i++) {
        writer.append(lines.get(i));
        writer.newLine();
      }
      writer.append(lines.get(lines.size() - 1));
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
