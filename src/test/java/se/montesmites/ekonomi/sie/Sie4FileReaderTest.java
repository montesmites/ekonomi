package se.montesmites.ekonomi.sie;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class Sie4FileReaderTest {

  private static final String PATH_TO_SIE_FILE = "/se/montesmites/ekonomi/sie/OVNINGSB.SE";

  @Test
  void countFlatRecords() throws Exception {
    var reader = new Sie4FileReader();
    var records = reader.read(Paths.get(getClass().getResource(PATH_TO_SIE_FILE).toURI()));
    var flatRecords =
        records.stream()
            .flatMap(record -> Stream.concat(Stream.of(record), record.getSubrecords().stream()))
            .collect(toList());
    assertEquals(2263, flatRecords.size());
  }

  @Test
  void countDeepRecords() throws Exception {
    var reader = new Sie4FileReader();
    var records = reader.read(Paths.get(getClass().getResource(PATH_TO_SIE_FILE).toURI()));
    System.out.println(records.stream().map(Object::toString).collect(joining("\n")));
    assertEquals(2045, records.size());
  }
}
