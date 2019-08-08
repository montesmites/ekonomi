package se.montesmites.ekonomi.sie;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

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
          .map(SieRecord::of)
          .collect(toList());
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
