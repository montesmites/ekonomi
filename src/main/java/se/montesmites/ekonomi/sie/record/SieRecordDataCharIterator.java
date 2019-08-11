package se.montesmites.ekonomi.sie.record;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

public class SieRecordDataCharIterator implements Iterator<Character> {

  private final Reader reader;
  private Character currentChar;

  SieRecordDataCharIterator(String recordData) {
    this.reader = new StringReader(recordData);
    currentChar = readNextChar();
  }

  @Override
  public boolean hasNext() {
    return currentChar != null;
  }

  @Override
  public Character next() {
    var previousChar = currentChar;
    currentChar = readNextChar();
    return previousChar;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  private Character readNextChar() {
    try {
      var i = reader.read();
      if (i == -1) {
        return null;
      } else {
        return (char) i;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
