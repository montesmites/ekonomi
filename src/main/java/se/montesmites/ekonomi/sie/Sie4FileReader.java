package se.montesmites.ekonomi.sie;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class Sie4FileReader {

  private static final String CHARSET_NAME = "IBM437";
  private static final Charset CHARSET = Charset.availableCharsets().get(CHARSET_NAME);

  List<SieRecord> read(Path path) {
    try (var lines = Files.lines(path, CHARSET)) {
      return lines
          .filter(not(String::isBlank))
          .map(SieFileLine::of)
          .reduce(
              Sie4FileReaderAggregator.empty(),
              Sie4FileReaderAggregator::accept,
              Sie4FileReaderAggregator::merge)
          .finish()
          .retrieveRecords();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
