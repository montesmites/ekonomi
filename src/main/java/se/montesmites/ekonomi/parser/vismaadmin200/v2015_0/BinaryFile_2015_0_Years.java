package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.DATE;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.INTEGER;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;

import java.time.LocalDate;
import java.util.List;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

public class BinaryFile_2015_0_Years extends BinaryFile_2015_0<Year> {

  private final Field<String> YEARID = new Field<>("yearid", STRING, 1, 1);
  private final Field<Integer> YEAR = new Field<>("year", INTEGER, 2, 4);
  private final Field<LocalDate> FROM = new Field<>("from", DATE, 2, 8);
  private final Field<LocalDate> TO = new Field<>("to", DATE, 10, 8);
  private final Field<String> STATUS =
      new Field<>("status", STRING, 0, 1) {
        @Override
        public boolean filter(Record record) {
          return super.filter(record) && !this.extract(record).equals("*");
        }
      };

  BinaryFile_2015_0_Years() {
    super("BOKFAAR.DBF", 513, 89);
  }

  @Override
  public List<Field<?>> getFields() {
    return List.of(STATUS, YEARID, YEAR, FROM, TO);
  }

  @Override
  public Year modelize(Record record) {
    return new Year(
        new YearId(YEARID.extract(record)),
        java.time.Year.of(YEAR.extract(record)),
        FROM.extract(record),
        TO.extract(record));
  }
}
