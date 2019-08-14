package se.montesmites.ekonomi.sie.record.types;

import static java.util.Arrays.stream;

import java.util.function.Function;
import se.montesmites.ekonomi.sie.record.SieRecord;

public enum SieRecordType {
  IB(TypeIB::of),
  KONTO(TypeKONTO::of),
  RAR(TypeRAR::of),
  RES(TypeRES::of);

  public static SieRecord specialize(SieRecord record) {
    return stream(values())
        .filter(type -> record.getLabel().equals(type.name()))
        .findAny()
        .map(type -> type.specializer.apply(record))
        .orElse(record);
  }

  private final Function<SieRecord, SieRecord> specializer;

  SieRecordType(Function<SieRecord, SieRecord> specializer) {
    this.specializer = specializer;
  }
}
