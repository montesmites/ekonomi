package se.montesmites.ekonomi.parser;

import java.util.Arrays;
import java.util.List;

class RecordDefinition {
    private final int firstBytePosition;
    private final int length;
    private final List<FieldDefinition> fields;

    public RecordDefinition(int firstBytePosition, int length, FieldDefinition... fields) {
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

    public List<FieldDefinition> getFields() {
        return fields;
    }
}
