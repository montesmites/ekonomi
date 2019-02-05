package se.montesmites.ekonomi.nikka;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

class ArgumentCaptor {

  @Argument(alias = "d", description = "destination folder for generated reports", required = true)
  private String destinationFolder;

  @Argument(alias = "y", description = "fiscal year for reports", required = true)
  private String year;

  @Argument(alias = "x", description = "path to xml definition of report", required = true)
  private String pathToXml;

  @Argument(alias = "t", description = "title of report", required = true)
  private String title;

  ArgumentCaptor(String[] args) {
    Args.parseOrExit(this, args);
  }

  Year year() {
    return Year.parse(year);
  }

  Path definitionPath() {
    return Paths.get(pathToXml);
  }

  private String title() {
    return title;
  }

  private Path destinationFolder() {
    return Paths.get(destinationFolder);
  }

  Path destinationPath() {
    var pathFormat = "%s %s %s.txt";
    return destinationFolder()
        .resolve(
            String.format(
                pathFormat,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")),
                title(),
                year()));
  }
}