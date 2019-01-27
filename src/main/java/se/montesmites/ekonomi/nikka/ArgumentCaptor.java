package se.montesmites.ekonomi.nikka;

import static java.util.Map.entry;
import static se.montesmites.ekonomi.nikka.ReportType.BALANCE_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.CASHFLOW_REPORT;
import static se.montesmites.ekonomi.nikka.ReportType.RESULT_REPORT;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

class ArgumentCaptor {

  private static final Map<ReportType, String> REPORT_TYPE_DESCRIPTIONS =
      Map.ofEntries(
          entry(CASHFLOW_REPORT, "Kassaflöde"),
          entry(RESULT_REPORT, "Resultaträkning"),
          entry(BALANCE_REPORT, "Balansräkning"));

  @Argument(alias = "f", description = "destination folder for generated reports", required = true)
  private String destinationFolder;

  @Argument(alias = "y", description = "fiscal year for reports", required = true)
  private String year;

  @Argument(
      alias = "t",
      description = "comma separated list of report types to generate",
      required = true)
  private String reportTypes;

  ArgumentCaptor(String[] args) {
    Args.parseOrExit(this, args);
  }

  Year year() {
    return Year.parse(year);
  }

  Supplier<Stream<ReportType>> reportTypes() {
    return () -> Arrays.stream(reportTypes.split(",")).map(ReportType::valueOf);
  }

  private Path destinationFolder() {
    return Paths.get(destinationFolder);
  }

  Path path(ReportType reportType) {
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
