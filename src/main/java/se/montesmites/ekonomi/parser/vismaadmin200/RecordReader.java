package se.montesmites.ekonomi.parser.vismaadmin200;

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
                chunk -> this.recordDefinition.getFields().stream().reduce(
                        new Record(),
                        (rec, def) -> def.populate(rec, chunk),
                        Record::merge));
    }

    private List<ByteChunk> byteChunks() {
        List<ByteChunk> ret = new ArrayList<>();
        for (int ix = recordDefinition.getFirstBytePosition();
                ix <= (bytes.length - recordDefinition.getLength());
                ix += recordDefinition.getLength()) {
            ret.add(new ByteChunk(recordDefinition, bytes.length, ix,
                    Arrays.copyOfRange(bytes, ix,
                            ix + recordDefinition.getLength())));
        }
        return ret;
    }
}
