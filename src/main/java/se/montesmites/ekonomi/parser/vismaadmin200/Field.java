package se.montesmites.ekonomi.parser.vismaadmin200;

public class Field<T> {

  private final FieldKey<T> key;
  private final int start;
  private final int length;

  public Field(String id, DataType<T> datatype, int start, int length) {
    this(new FieldKey<>(id, datatype), start, length);
  }

  private Field(FieldKey<T> key, int start, int length) {
    this.key = key;
    this.start = start;
    this.length = length;
  }

  final Record populate(Record record, ByteChunk chunk) {
    return record.merge(key.set(key.getDatatype().read(chunk, start, length)));
  }

  public boolean filter(Record record) {
    return record.get(key).isPresent();
  }

  public T extract(Record record) {
    return record.extract(key);
  }
}
