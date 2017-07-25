package se.montesmites.ekonomi.parser;

class Field<T> {

    final static <T> Field<T> define(String id, DataType<T> datatype, int start, int length) {
        return new Field<>(new FieldKey<>(id, datatype), start, length);
    }

    protected final FieldKey<T> key;
    protected final int start;
    protected final int length;

    private Field(FieldKey<T> key, int start, int length) {
        this.key = key;
        this.start = start;
        this.length = length;
    }

    final Record populate(Record record, byte[] bytes) {
        return record.merge(
                key.set(key.getDatatype().read(bytes, start, length)));
    }

    boolean filter(Record record) {
        return record.get(key).isPresent();
    }

    T extract(Record record) {
        return record.extract(key);
    }
}
