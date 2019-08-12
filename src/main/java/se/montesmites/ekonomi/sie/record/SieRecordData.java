package se.montesmites.ekonomi.sie.record;

import java.util.List;

public class SieRecordData {

  public static SieRecordData of(String recordData) {
    return new SieRecordData(recordData);
  }

  private final List<SieToken> tokens;

  private SieRecordData(String recordData) {
    this.tokens = List.copyOf(SieRecordTokenizer.of().tokenize(recordData));
  }

  public List<SieToken> getTokens() {
    return List.copyOf(tokens);
  }

  public SieToken get(int index) {
    return tokens.get(index);
  }

  @Override
  public String toString() {
    var b = new StringBuilder();
    for (var token : tokens) {
      b.append(" ");
      b.append(token.asString());
    }
    return b.substring(1);
  }
}
