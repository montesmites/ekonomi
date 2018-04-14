package se.montesmites.ekonomi.parser.vismaadmin200;

import java.util.List;

public class RecordDefinition {

    private final int firstBytePosition;
    private final int length;
    private final List<Field<?>> fields;

    public RecordDefinition(int firstBytePosition, int length, List<Field<?>> fields) {
        this.firstBytePosition = firstBytePosition;
        this.length = length;
        this.fields = fields;
    }

    int getFirstBytePosition() {
        return firstBytePosition;
    }

    int getLength() {
        return length;
    }

    List<Field<?>> getFields() {
        return fields;
    }
}
