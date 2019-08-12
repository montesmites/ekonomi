package se.montesmites.ekonomi.sie.record;

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
    return new LabelTokenizer();
  }

  private static class LabelTokenizer extends SieRecordTokenizer {

    @Override
    public List<SieToken> tokenize(String line) {
      return List.of();
    }
  }

  private SieRecordTokenizer() {
  }

  public abstract List<SieToken> tokenize(String line);
}
