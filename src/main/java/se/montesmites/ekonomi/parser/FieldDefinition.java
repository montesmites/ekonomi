package se.montesmites.ekonomi.parser;

import java.time.LocalDate;
import java.util.Optional;
import static se.montesmites.ekonomi.parser.ByteReader.*;
import static se.montesmites.ekonomi.parser.FieldKey.*;

abstract class FieldDefinition<T> {

    public final static class StringField extends FieldDefinition<String> {

        public StringField(String id, int start, int length) {
            super(new StringKey(id), start, length);
        }

        @Override
        public Optional<String> read(byte[] bytes) {
            return new StringReader().read(bytes, start, length);
        }

    }
    
    public final static class DateField extends FieldDefinition<LocalDate> {
        
        public DateField(String id, int start, int length) {
            super(new DateKey(id), start, length);
        }

        @Override
        public Optional<LocalDate> read(byte[] bytes) {
            return new DateReader().read(bytes, start, length);
        }
        
    }

    protected final FieldKey<T> key;
    protected final int start;
    protected final int length;

    private FieldDefinition(FieldKey<T> key, int start, int length) {
        this.key = key;
        this.start = start;
        this.length = length;
    }

    abstract Optional<T> read(byte[] bytes);

    final Record populate(Record record, byte[] bytes) {
        return record.merge(key.set(this.read(bytes)));
    }
    
    boolean filter(Record record) {
        return record.get(key).isPresent();
    }
    
    T extract(Record record) {
        return record.extract(key);
    }
}
