package se.montesmites.ekonomi.parser;

import java.util.Objects;
import java.util.Optional;

class FieldKey<T> {

    private final String id;
    private final DataType<T> datatype;

    FieldKey(String id, DataType<T> datatype) {
        this.id = id;
        this.datatype = datatype;
    }
    
    DataType<T> getDatatype() {
        return this.datatype;
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
