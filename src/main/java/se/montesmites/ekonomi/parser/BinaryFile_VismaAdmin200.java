package se.montesmites.ekonomi.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface BinaryFile_VismaAdmin200<T> {

    public List<T> parse(Path path);
    String getFileName();

    RecordDefinition getRecordDefinition();

    default boolean filter(Record record) {
        return this.getRecordDefinition().getFields().stream().allMatch(
                def -> def.filter(record));
    }

    T extract(Record record);

    default byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
