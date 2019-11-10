package se.montesmites.ekonomi.nikka;

import java.nio.file.Paths;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.configuration.EkonomiProperties;
import se.montesmites.ekonomi.datasource.DatabaseDatasource;
import se.montesmites.ekonomi.datasource.DatasourceType;
import se.montesmites.ekonomi.report.DataFetcher;

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
    // Main.main(new String[]{});
    if (properties.getDatasourceType() == DatasourceType.DATABASE) {
      var main = new Main();
      var report =
          main.generateReport(
              new DataFetcher(databaseFetcher.fetchOrganization()),
              Paths.get(properties.getTemplatePath()),
              java.time.Year.of(properties.getFiscalYear()));
      main.renderToFile(
          report,
          ArgumentCaptor.destinationPath(
              Paths.get(properties.getOutputDir()),
              properties.getTitle(),
              java.time.Year.of(properties.getFiscalYear())));
    }
  }
}
