package se.montesmites.ekonomi.parser.vismaadmin200;

import java.util.Arrays;

public class ByteChunk {

    private final RecordDefinition recordDefinition;
    private final int fileLength;
    private final int position;
    private final byte[] bytes;

    public ByteChunk(RecordDefinition recordDefinition, int fileLength, int position, byte[] bytes) {
        this.recordDefinition = recordDefinition;
        this.fileLength = fileLength;
        this.position = position;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        String fmt = "{%d, %d} of %d at [%d] yields %s";
        String msg = String.format(fmt,
                recordDefinition.getFirstBytePosition(),
                recordDefinition.getLength(),
                fileLength,
                position,
                Arrays.toString(bytes));
        return msg;
    }
}
