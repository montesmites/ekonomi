package se.montesmites.ekonomi.sie.file;

import java.util.List;
import java.util.function.Supplier;
import se.montesmites.ekonomi.sie.record.SieRecordTokenizer;
import se.montesmites.ekonomi.sie.record.SieToken;

public enum SieFileLineType {
  PROSAIC(SieRecordTokenizer::of),
  BEGIN_SUBRECORDS(SieRecordTokenizer::empty),
  END_SUBRECORDS(SieRecordTokenizer::empty);

  private final Supplier<SieRecordTokenizer> tokenizer;

  SieFileLineType(Supplier<SieRecordTokenizer> tokenizer) {
    this.tokenizer = tokenizer;
  }

  public final List<SieToken> tokenize(SieFileLine line) {
    return tokenizer.get().tokenize(line.getLine());
  }
}
