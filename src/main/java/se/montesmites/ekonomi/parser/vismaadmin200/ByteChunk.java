package se.montesmites.ekonomi.parser.vismaadmin200;

import java.util.Arrays;

class ByteChunk {

  private final RecordDefinition recordDefinition;
  private final int fileLength;
  private final int position;
  private final byte[] bytes;

  ByteChunk(RecordDefinition recordDefinition, int fileLength, int position, byte[] bytes) {
    this.recordDefinition = recordDefinition;
    this.fileLength = fileLength;
    this.position = position;
    this.bytes = bytes;
  }

  byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return String.format(
        "{%d, %d} of %d at [%d] yields %s",
        recordDefinition.getFirstBytePosition(),
        recordDefinition.getLength(),
        fileLength,
        position,
        Arrays.toString(bytes));
  }
}
