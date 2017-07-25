package se.montesmites.ekonomi.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RecordReader {

    private final RecordDefinition recordDefinition;
    private final byte[] bytes;

    public RecordReader(RecordDefinition recordDefinition, byte[] bytes) {
        this.recordDefinition = recordDefinition;
        this.bytes = bytes;
    }

    public Stream<Record> allRecordsAsStream() {
        return byteChunks().stream().map(
                b -> this.recordDefinition.getFields().stream().reduce(
                        new Record(),
                        (rec, def) -> def.populate(rec, b),
                        (rec1, rec2) -> rec1.merge(rec2)));
    }

    private List<byte[]> byteChunks() {
        List<byte[]> ret = new ArrayList<>();
        for (int ix = recordDefinition.getFirstBytePosition(); ix <= (bytes.length - recordDefinition.getLength()); ix += recordDefinition.getLength()) {
            ret.add(Arrays.copyOfRange(bytes, ix,
                    ix + recordDefinition.getLength()));
        }
        return ret;
    }
}
