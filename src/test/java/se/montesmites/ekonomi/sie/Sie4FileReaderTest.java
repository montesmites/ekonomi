package se.montesmites.ekonomi.sie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class Sie4FileReaderTest {

  private static final String PATH_TO_SIE_FILE = "/se/montesmites/ekonomi/sie/OVNINGSB.SE";

  @Test
  void countFlatRecords() throws Exception {
    var parser = new Sie4FileReader();
    var records = parser.read(Paths.get(getClass().getResource(PATH_TO_SIE_FILE).toURI()));
    assertEquals(2387, records.size());
  }
}
