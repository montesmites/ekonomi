package se.montesmites.ekonomi.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import static se.montesmites.ekonomi.parser.Field.*;

abstract class BinaryFile_2015_0<T> {

    public final static BinaryFile_2015_0 YEARS = new BinaryFile_2015_0<Year>() {
        private final StringField STATUS = new StringField("status", 0, 1);
        private final StringField YEARID = new StringField("yearid", 1, 1);
        private final StringField YEAR = new StringField("year", 2, 4);
        private final DateField FROM = new DateField("from", 2, 8);
        private final DateField TO = new DateField("to", 10, 8);

        @Override
        String getFileName() {
            return "BOKFAAR.DBF";
        }

        @Override
        RecordDefinition getRecordDefinition() {
            return new RecordDefinition(513, 89, STATUS, YEARID,
                    YEAR, FROM, TO);
        }

        @Override
        Year extract(Record rec) {
            return new Year(new YearId(YEARID.extract(rec)), YEAR.extract(rec),
                    FROM.extract(rec), TO.extract(rec));
        }
    };

    private BinaryFile_2015_0() {
    }

    abstract String getFileName();

    abstract RecordDefinition getRecordDefinition();

    boolean filter(Record record) {
        return this.getRecordDefinition().getFields().stream().allMatch(
                def -> def.filter(record));
    }

    abstract T extract(Record record);

    List<T> parse(Path path) {
        Path p = path.resolve(this.getFileName());
        RecordReader rr = new RecordReader(
                this.getRecordDefinition(), readAllBytes(p));
        return rr.allRecordsAsStream().filter(this::filter).map(
                this::extract).collect(
                        Collectors.toList());
    }

    private byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
