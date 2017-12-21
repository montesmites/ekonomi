package se.montesmites.ekonomi.parser.vismaadmin200;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface BinaryFile_VismaAdmin200<T> {

    default Stream<T> parse(Path path) {
        Path p = path.resolve(this.getFileName());
        RecordReader rr = new RecordReader(
                this.getRecordDefinition(), readAllBytes(p));
        return rr.allRecordsAsStream().filter(this::filter).map(
                this::modelize);
    }

    String getFileName();

    RecordDefinition getRecordDefinition();

    default boolean filter(Record record) {
        return this.getRecordDefinition().getFields().stream().allMatch(
                def -> def.filter(record));
    }

    public T modelize(Record record);

    default byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
