package se.montesmites.ekonomi.parser;

import java.time.LocalDate;
import static se.montesmites.ekonomi.parser.FieldKey.*;

abstract class Field<T> {

    public final static class StringField extends Field<String> {

        public StringField(String id, int start, int length) {
            super(new StringKey(id), start, length);
        }
    }

    public final static class DateField extends Field<LocalDate> {

        public DateField(String id, int start, int length) {
            super(new DateKey(id), start, length);
        }
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
