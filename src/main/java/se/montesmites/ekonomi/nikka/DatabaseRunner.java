package se.montesmites.ekonomi.nikka;

import java.nio.file.Paths;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.configuration.EkonomiProperties;
import se.montesmites.ekonomi.datasource.DatabaseDataSource;
import se.montesmites.ekonomi.report.DataFetcher;

@Component
@Profile("!test")
public class DatabaseRunner implements ApplicationRunner {

  private final EkonomiProperties properties;
  private final DatabaseDataSource databaseFetcher;

  public DatabaseRunner(EkonomiProperties properties, DatabaseDataSource databaseFetcher) {
    this.properties = properties;
    this.databaseFetcher = databaseFetcher;
  }

  @Override
  public void run(ApplicationArguments applicationArguments) {
    // Main.main(new String[]{});
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
