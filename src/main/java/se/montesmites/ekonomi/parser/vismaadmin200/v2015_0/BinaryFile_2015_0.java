package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.DataType;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;
import se.montesmites.ekonomi.parser.vismaadmin200.RecordDefinition;

abstract class BinaryFile_2015_0<T> implements BinaryFile_VismaAdmin200<T> {

    public final static BinaryFile_2015_0<Year> YEARS
            = new BinaryFile_2015_0<Year>("BOKFAAR.DBF", 513, 89) {
        private final Field<String> STATUS = Field.define("status",
                DataType.STRING, 0, 1);
        private final Field<String> YEARID = Field.define("yearid",
                DataType.STRING, 1, 1);
        private final Field<String> YEAR = Field.define("year", DataType.STRING,
                2, 4);
        private final Field<LocalDate> FROM = Field.define("from",
                DataType.DATE, 2, 8);
        private final Field<LocalDate> TO = Field.define("to", DataType.DATE,
                10, 8);

        @Override
        public List<Field<?>> getFields() {
            return Arrays.asList(STATUS, YEARID, YEAR, FROM, TO);
        }

        @Override
        public Year modelize(Record record) {
            return new Year(
                    new YearId(YEARID.extract(record)),
                    YEAR.extract(record),
                    FROM.extract(record),
                    TO.extract(record));
        }
    };

    private final String fileName;
    private final int start;
    private final int length;

    private BinaryFile_2015_0(String fileName, int start, int length) {
        this.fileName = fileName;
        this.start = start;
        this.length = length;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public RecordDefinition getRecordDefinition() {
        return new RecordDefinition(start, length, getFields());
    }

    abstract List<Field<?>> getFields();
}
