package se.montesmites.ekonomi.sie.record;

import java.util.ArrayList;
import java.util.List;

public class SieRecordData {

  public static SieRecordData of(String recordData) {
    return new SieRecordData(recordData);
  }

  private ArrayList<SieToken> tokens;

  private SieRecordData(String recordData) {
    this.tokens = new ArrayList<>();
    tokenize(recordData);
  }

  public List<SieToken> getTokens() {
    return List.copyOf(tokens);
  }

  public SieToken get(int index) {
    return tokens.get(index);
  }

  private void tokenize(String recordData) {
    var iter = new SieRecordDataCharIterator(recordData);
    var token = new StringBuilder();
    var prevChar = ' ';
    var openQuote = false;
    while (iter.hasNext()) {
      char c = iter.next();
      if (!openQuote && isWhiteSpace(c)) {
        token = addToken(token);
      } else {
        if (c != '\\') {
          if (prevChar != '\\' && c == '"') {
            if (!openQuote) {
              openQuote = true;
            } else {
              openQuote = false;
              token = addToken(token);
            }
          } else {
            token.append(c);
          }
        }
      }
      prevChar = c;
    }
    addToken(token);
  }

  private StringBuilder addToken(StringBuilder token) {
    if (token.length() > 0) {
      tokens.add(SieToken.of(token.toString()));
    }
    return new StringBuilder();
  }

  private boolean isWhiteSpace(char c) {
    return c == ' ' || c == '\t';
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
