package se.montesmites.ekonomi.sie.record;

import java.util.ArrayList;
import java.util.List;

public abstract class SieRecordTokenizer {

  public static SieRecordTokenizer empty() {
    return new SieRecordTokenizer() {
      @Override
      public List<SieToken> tokenize(String line) {
        return List.of();
      }
    };
  }

  public static SieRecordTokenizer of() {
    return new DefaultTokenizer();
  }

  private static class DefaultTokenizer extends SieRecordTokenizer {

    @Override
    public List<SieToken> tokenize(String line) {
      return doTokenize(line);
    }
  }

  private SieRecordTokenizer() {
  }

  public abstract List<SieToken> tokenize(String text);

  private List<SieToken> tokens = new ArrayList<>();

  List<SieToken> doTokenize(String recordData) {
    var iter = recordData.chars().mapToObj(ch -> (char) ch).iterator();
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
    return List.copyOf(tokens);
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
}
