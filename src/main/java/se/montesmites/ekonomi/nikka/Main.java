package se.montesmites.ekonomi.nikka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.xml.JaxbReportBuilder;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

class Main {

  public static void main(String[] _args) {
    var args =
        "-t Kassaflöde -x D:\\git\\ekonomi\\src\\main\\resources\\se\\montesmites\\ekonomi\\nikka\\nikka-cashflow-report-definition.xml -y 2018 -d C:\\temp\\nikka\\reports\\2018"
            .split(" ");
    var arguments = new ArgumentCaptor(args);
    var main = new Main(fromDataPath());
    // var main = new Main(fromSiePath());
    var report = main.generateReport(arguments);
    main.renderToFile(report, arguments.destinationPath());
  }

  private static Organization fromDataPath() {
    var dataPath = Paths.get("C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka");
    return new OrganizationBuilder(dataPath).build();
  }

  private static Organization fromSiePath(Path path) {
    var siePath =
        Paths.get(
            "C:\\ProgramData\\SPCS\\SPCS Administration\\Företag\\nikka\\sie\\2019-08-18 NIKLAS_K.SE");
    return SieToOrganizationConverter.of().convert(siePath);
  }

  private final DataFetcher dataFetcher;

  private Main(Organization organization) {
    this.dataFetcher = new DataFetcher(organization);
  }

  private Report generateReport(ArgumentCaptor arguments) {
    return new JaxbReportBuilder(arguments.definitionPath()).report(dataFetcher, arguments.year());
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
