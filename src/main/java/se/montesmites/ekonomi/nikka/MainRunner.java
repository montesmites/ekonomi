package se.montesmites.ekonomi.nikka;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.datasource.DatabaseDataSource;

@Component
@Profile("!test")
public class MainRunner implements ApplicationRunner {

  private final DatabaseDataSource databaseFetcher;

  public MainRunner(DatabaseDataSource databaseFetcher) {
    this.databaseFetcher = databaseFetcher;
  }

  @Override
  public void run(ApplicationArguments applicationArguments) {
    // Main.main(new String[]{});
    var args =
        "-t Kassaflöde -x D:\\git\\ekonomi\\src\\main\\resources\\se\\montesmites\\ekonomi\\nikka\\nikka-cashflow-report-definition.xml -y 2019 -d C:\\temp\\nikka\\reports\\2019"
            .split(" ");
    var arguments = new ArgumentCaptor(args);
    var main = new Main(databaseFetcher.fetchOrganization());
    var report = main.generateReport(arguments);
    main.renderToFile(report, arguments.destinationPath());
  }
}
