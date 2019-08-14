package se.montesmites.ekonomi.sie.record.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.montesmites.ekonomi.sie.file.Sie4FileReader;

class RecordTypesTest {

  private static final String PATH_TO_SIE_FILE = "/se/montesmites/ekonomi/sie/OVNINGSB.SE";

  private static Stream<Arguments> params() {
    return Stream.of(
        Arguments.of(TypeKONTO.class, 590),
        Arguments.of(TypeRAR.class, 2),
        Arguments.of(TypeIB.class, 52),
        Arguments.of(TypeRES.class, 91),
        Arguments.of(TypeVER.class, 62));
  }

  private Path pathToSieFile;

  @BeforeEach
  void beforeEach() throws Exception {
    pathToSieFile = Paths.get(RecordTypesTest.class.getResource(PATH_TO_SIE_FILE).toURI());
  }

  @ParameterizedTest
  @MethodSource(value = "params")
  void count(Class<?> clazz, int expectedCount) {
    var reader = new Sie4FileReader();
    var records = reader.read(pathToSieFile);
    var act = records.stream().filter(clazz::isInstance).count();
    assertEquals(expectedCount, act);
  }
}
