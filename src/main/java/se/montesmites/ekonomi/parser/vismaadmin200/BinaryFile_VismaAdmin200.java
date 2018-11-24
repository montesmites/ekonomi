package se.montesmites.ekonomi.parser.vismaadmin200;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface BinaryFile_VismaAdmin200<T> {

  default Stream<T> parse(Path path) {
    var filePath = path.resolve(this.getFileName());
    var recordReader = new RecordReader(this.getRecordDefinition(), readAllBytes(filePath));
    return recordReader.allRecordsAsStream().filter(this::filter).map(this::modelize);
  }

  String getFileName();

  RecordDefinition getRecordDefinition();

  default boolean filter(Record record) {
    return this.getRecordDefinition().getFields().stream().allMatch(def -> def.filter(record));
  }

  T modelize(Record record);

  default byte[] readAllBytes(Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
