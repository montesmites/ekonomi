package se.montesmites.ekonomi.parser;

import java.util.Arrays;
import java.util.List;

class RecordDefinition {

    private final int firstBytePosition;
    private final int length;
    private final List<Field> fields;

    public RecordDefinition(int firstBytePosition, int length, Field... fields) {
        this.firstBytePosition = firstBytePosition;
        this.length = length;
        this.fields = Arrays.asList(fields);
    }

    public int getFirstBytePosition() {
        return firstBytePosition;
    }

    public int getLength() {
        return length;
    }

    public List<Field> getFields() {
        return fields;
    }
}
