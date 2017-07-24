package se.montesmites.ekonomi.parser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import static se.montesmites.ekonomi.parser.DataType.*;

abstract class FieldKey<T> {

    public final static class StringKey extends FieldKey<String> {

        public StringKey(String id) {
            super(id, new StringType());
        }

    }

    public final static class DateKey extends FieldKey<LocalDate> {

        public DateKey(String id) {
            super(id, new DateType());
        }
    }

    private final String id;

    private FieldKey(String id, DataType<T> datatype) {
        this.id = id;
    }

    public T asInstanceOf(Object value) {
        return (T) value;
    }

    public Record set(Optional<T> value) {
        return new Record(this, value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FieldKey<?> other = (FieldKey<?>) obj;
        return Objects.equals(this.id, other.id);
    }
}
