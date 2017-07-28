package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Series;
import se.montesmites.ekonomi.model.YearId;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.DATE;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.INTEGER;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.STRING;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;

public class BinaryFile_2015_0_Events extends BinaryFile_2015_0<Event> {

    public BinaryFile_2015_0_Events() {
        super("VER.DBF", 962, 293);
    }
    private final Field<String> YEARID = new Field("yearid", STRING, 0, 1);
    private final Field<String> SERIES = new Field("series", STRING, 222, 1);
    private final Field<LocalDate> EDATE = new Field("date", DATE, 8, 8);
    private final Field<LocalDate> RDATE = new Field("regdate", DATE, 16, 8);
    private final Field<String> DESCR = new Field("descr", STRING, 32, 60);
    private final Field<Integer> ID = new Field("id", INTEGER, 1, 7) {
        @Override
        public boolean filter(Record record) {
            return super.filter(record) && ID.extract(record) > 0;
        }
    };

    @Override
    List<Field<?>> getFields() {
        return Arrays.asList(YEARID, ID, SERIES, EDATE, RDATE, DESCR);
    }

    @Override
    public Event modelize(Record record) {
        YearId yearid = new YearId(YEARID.extract(record));
        Series series = new Series(SERIES.extract(record));
        EventId eventid = new EventId(yearid, ID.extract(record), series);
        return new Event(
                eventid,
                EDATE.extract(record),
                DESCR.extract(record).trim(),
                RDATE.extract(record));
    }
}
