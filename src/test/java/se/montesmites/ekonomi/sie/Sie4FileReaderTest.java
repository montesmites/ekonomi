package se.montesmites.ekonomi.sie;

import static java.util.Map.entry;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Sie4FileReaderTest {

  private static final String PATH_TO_SIE_FILE = "/se/montesmites/ekonomi/sie/OVNINGSB.SE";
  private Path pathToSieFile;

  @BeforeEach
  void beforeEach() throws Exception {
    pathToSieFile = Paths.get(Sie4FileReaderTest.class.getResource(PATH_TO_SIE_FILE).toURI());
  }

  @Test
  void countFlatRecords() {
    var reader = new Sie4FileReader();
    var records = reader.read(pathToSieFile);
    var flatRecords =
        records.stream()
            .flatMap(record -> Stream.concat(Stream.of(record), record.getSubrecords().stream()))
            .collect(toList());
    assertEquals(2263, flatRecords.size());
  }

  @Test
  void countDeepRecords() {
    var reader = new Sie4FileReader();
    var records = reader.read(pathToSieFile);
    assertEquals(2045, records.size());
  }

  @Test
  void groupByLabel() {
    var expectedFrequencies =
        Map.ofEntries(
            entry("ADRESS", 1L),
            entry("DIM", 1L),
            entry("FLAGGA", 1L),
            entry("FNAMN", 1L),
            entry("FNR", 1L),
            entry("FORMAT", 1L),
            entry("GEN", 1L),
            entry("IB", 52L),
            entry("KONTO", 590L),
            entry("KPTYP", 1L),
            entry("KTYP", 590L),
            entry("OBJEKT", 2L),
            entry("ORGNR", 1L),
            entry("PROGRAM", 1L),
            entry("RAR", 2L),
            entry("RES", 91L),
            entry("SIETYP", 1L),
            entry("SRU", 587L),
            entry("TAXAR", 1L),
            entry("UB", 56L),
            entry("VALUTA", 1L),
            entry("VER", 62L));
    var reader = new Sie4FileReader();
    var records = reader.read(pathToSieFile);
    var groupByLabel = records.stream().collect(groupingBy(SieRecord::getLabel, counting()));
    assertEquals(expectedFrequencies, groupByLabel);
  }
}
