package se.montesmites.ekonomi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.configuration.EkonomiProperties;
import se.montesmites.ekonomi.datasource.DatabaseDatasource;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.report.DataFetcher;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.xml.JaxbReportBuilder;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

@Component
@Profile("!test")
public class ReportGenerator implements ApplicationRunner {

  private final EkonomiProperties properties;
  private final DatabaseDatasource databaseFetcher;

  public ReportGenerator(EkonomiProperties properties, DatabaseDatasource databaseFetcher) {
    this.properties = properties;
    this.databaseFetcher = databaseFetcher;
  }

  @Override
  public void run(ApplicationArguments applicationArguments) {
    renderToFile(
        generateReport(
            dataFetcher(),
            properties.getReport().getTemplate().asPath(),
            java.time.Year.of(properties.getReport().getFiscalYear())),
        destinationPath(
            Paths.get(properties.getReport().getOutputDir()),
            properties.getReport().getTitle(),
            java.time.Year.of(properties.getReport().getFiscalYear())));
  }

  private DataFetcher dataFetcher() {
    switch (properties.getDatasource().getType()) {
      case DATABASE:
        return new DataFetcher(databaseFetcher.fetchOrganization());
      case SIE:
        return new DataFetcher(
            SieToOrganizationConverter.of()
                .convert(Paths.get(properties.getDatasource().getSieInputPath())));
      case SPCS:
        return new DataFetcher(
            new OrganizationBuilder(Paths.get(properties.getDatasource().getSpcsInputDir()))
                .build());
      default:
        throw new IllegalArgumentException(properties.getDatasource().getType().name());
    }
  }

  private Path destinationPath(Path outputDir, String title, java.time.Year year) {
    var pathFormat = "%s %s %d.txt";
    return outputDir.resolve(
        String.format(
            pathFormat,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
            title,
            year.getValue()));
  }

  private Report generateReport(DataFetcher dataFetcher, Path template, java.time.Year year) {
    return new JaxbReportBuilder(template).report(dataFetcher, year);
  }

  private void renderToFile(Report report, Path outputPath) {
    try (var writer = Files.newBufferedWriter(outputPath)) {
      Files.createDirectories(outputPath.getParent());
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
