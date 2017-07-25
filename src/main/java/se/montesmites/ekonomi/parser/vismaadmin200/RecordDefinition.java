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

    public int getFirstBytePosition() {
        return firstBytePosition;
    }

    public int getLength() {
        return length;
    }

    public List<Field<?>> getFields() {
        return fields;
    }
}
