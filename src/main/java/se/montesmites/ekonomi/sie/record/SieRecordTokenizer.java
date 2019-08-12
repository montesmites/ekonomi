package se.montesmites.ekonomi.sie.record;

import static se.montesmites.ekonomi.sie.util.ListUtil.append;

import java.util.List;

public abstract class SieRecordTokenizer {

  private static boolean isWhiteSpace(char c) {
    return c == ' ' || c == '\t';
  }

  private static boolean isEscape(char c) {
    return c == '\\';
  }

  private static boolean isUnscapedQuote(char previous, char current) {
    return previous != '\\' && current == '"';
  }

  public static SieRecordTokenizer empty() {
    return new SieRecordTokenizer() {
      @Override
      SieRecordTokenizer tokenize(char previous, char current) {
        return this;
      }

      @Override
      List<SieToken> retrieveTokens() {
        return List.of();
      }
    };
  }

  public static SieRecordTokenizer of() {
    return new DefaultTokenizer(List.of());
  }

  private static class DefaultTokenizer extends SieRecordTokenizer {

    private final StringBuilder token = new StringBuilder();
    private final List<SieToken> tokens;

    private DefaultTokenizer(List<SieToken> tokens) {
      this.tokens = tokens;
    }

    @Override
    public SieRecordTokenizer tokenize(char previous, char current) {
      if (isWhiteSpace(current)) {
        return new DefaultTokenizer(retrieveTokens());
      } else if (isEscape(current)) {
        return this;
      } else if (isUnscapedQuote(previous, current)) {
        return new QuotedTokenizer(tokens);
      } else {
        token.append(current);
        return this;
      }
    }

    @Override
    List<SieToken> retrieveTokens() {
      if (token.length() > 0) {
        return List.copyOf(append(tokens, retrieveToken()));
      } else {
        return List.copyOf(tokens);
      }
    }

    private SieToken retrieveToken() {
      return SieToken.of(token.toString());
    }
  }

  private static class QuotedTokenizer extends SieRecordTokenizer {

    private final StringBuilder token = new StringBuilder();
    private final List<SieToken> tokens;

    private QuotedTokenizer(List<SieToken> tokens) {
      this.tokens = tokens;
    }

    @Override
    SieRecordTokenizer tokenize(char previous, char current) {
      if (isEscape(current)) {
        return this;
      } else if (isUnscapedQuote(previous, current)) {
        return new DefaultTokenizer(retrieveTokens());
      } else {
        token.append(current);
        return this;
      }
    }

    @Override
    List<SieToken> retrieveTokens() {
      if (token.length() > 0) {
        return List.copyOf(append(tokens, retrieveToken()));
      } else {
        return List.copyOf(tokens);
      }
    }

    private SieToken retrieveToken() {
      return SieToken.of(token.toString());
    }
  }

  private SieRecordTokenizer() {
  }

  public final List<SieToken> tokenize(String text) {
    var iter = text.chars().mapToObj(ch -> (char) ch).iterator();
    var prevChar = ' ';
    var tokenizer = SieRecordTokenizer.of();
    while (iter.hasNext()) {
      char c = iter.next();
      tokenizer = tokenizer.tokenize(prevChar, c);
      prevChar = c;
    }
    return List.copyOf(tokenizer.retrieveTokens());
  }

  abstract SieRecordTokenizer tokenize(char previous, char current);

  abstract List<SieToken> retrieveTokens();
}
