package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.DataType;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import se.montesmites.ekonomi.parser.vismaadmin200.Record;
import se.montesmites.ekonomi.parser.vismaadmin200.RecordDefinition;
import se.montesmites.ekonomi.parser.vismaadmin200.RecordReader;

abstract class BinaryFile_2015_0<T> implements BinaryFile_VismaAdmin200<T> {

    public final static BinaryFile_2015_0 YEARS = new BinaryFile_2015_0<Year>() {
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
        public String getFileName() {
            return BinaryFile_2015_0_Definition.YEARS.getFileName();
        }

        @Override
        public RecordDefinition getRecordDefinition() {
            return new RecordDefinition(
                    BinaryFile_2015_0_Definition.YEARS.getStart(),
                    BinaryFile_2015_0_Definition.YEARS.getLength(),
                    Arrays.asList(STATUS,
                            YEARID,
                            YEAR, FROM, TO));
        }

        @Override
        public Year extract(Record rec) {
            return new Year(new YearId(YEARID.extract(rec)), YEAR.extract(rec),
                    FROM.extract(rec), TO.extract(rec));
        }
    };

    private BinaryFile_2015_0() {
    }

    @Override
    public List<T> parse(Path path) {
        Path p = path.resolve(this.getFileName());
        RecordReader rr = new RecordReader(
                this.getRecordDefinition(), readAllBytes(p));
        return rr.allRecordsAsStream().filter(this::filter).map(
                this::extract).collect(
                        Collectors.toList());
    }
}
