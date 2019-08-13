package se.montesmites.ekonomi.sie.record;

import static se.montesmites.ekonomi.sie.util.ListUtil.append;
import static se.montesmites.ekonomi.sie.util.ListUtil.concat;

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
      SieRecordTokenizer tokenize(char current) {
        return this;
      }

      @Override
      List<SieToken> retrieveTokens() {
        return List.of();
      }
    };
  }

  public static SieRecordTokenizer of() {
    return new DefaultTokenizer(new StringBuilder(), List.of(), ' ');
  }

  private static class DefaultTokenizer extends SieRecordTokenizer {

    private final char previous;
    private final StringBuilder token;
    private final List<SieToken> tokens;

    private DefaultTokenizer(StringBuilder token, List<SieToken> tokens, char previous) {
      this.token = token;
      this.tokens = tokens;
      this.previous = previous;
    }

    @Override
    public SieRecordTokenizer tokenize(char current) {
      if (isWhiteSpace(current)) {
        return new DefaultTokenizer(new StringBuilder(), retrieveTokens(), current);
      } else if (isEscape(current)) {
        return this;
      } else if (isUnscapedQuote(previous, current)) {
        return new QuotedTokenizer(new StringBuilder(), tokens, current);
      } else {
        token.append(current);
        return new DefaultTokenizer(token, List.copyOf(tokens), current);
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

    private final char previous;
    private final StringBuilder token;
    private final List<SieToken> tokens;

    private QuotedTokenizer(StringBuilder token, List<SieToken> tokens, char previous) {
      this.token = token;
      this.tokens = tokens;
      this.previous = previous;
    }

    @Override
    SieRecordTokenizer tokenize(char current) {
      if (isEscape(current)) {
        return new QuotedTokenizer(token, tokens, current);
      } else if (isUnscapedQuote(previous, current)) {
        return new DefaultTokenizer(new StringBuilder(), retrieveTokens(), current);
      } else {
        token.append(current);
        return new QuotedTokenizer(token, List.copyOf(tokens), current);
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
    return text.chars()
        .mapToObj(ch -> (char) ch)
        .reduce(SieRecordTokenizer.of(), SieRecordTokenizer::tokenize, SieRecordTokenizer::merge)
        .retrieveTokens();
  }

  abstract SieRecordTokenizer tokenize(char current);

  abstract List<SieToken> retrieveTokens();

  private SieRecordTokenizer merge(SieRecordTokenizer that) {
    return new DefaultTokenizer(
        new StringBuilder(), concat(this.retrieveTokens(), that.retrieveTokens()), ' ');
  }
}
