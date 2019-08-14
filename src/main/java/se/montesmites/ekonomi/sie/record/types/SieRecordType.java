package se.montesmites.ekonomi.sie.record.types;

import static java.util.Arrays.stream;

import java.util.function.Function;
import se.montesmites.ekonomi.sie.record.SieRecord;

public enum SieRecordType {
  IB(
      record ->
          new TypeIB(
              record,
              record.recordData().get(0).asInt(),
              record.recordData().get(1).asString(),
              record.recordData().get(2).asCurrency())),
  KONTO(
      record ->
          new TypeKONTO(
              record,
              record.recordData().get(0).asString(),
              record.recordData().get(1).asString())),
  RAR(
      record ->
          new TypeRAR(
              record,
              record.recordData().get(0).asInt(),
              record.recordData().get(1).asDate(),
              record.recordData().get(2).asDate())),
  RES(
      record ->
          new TypeRES(
              record,
              record.recordData().get(0).asInt(),
              record.recordData().get(1).asString(),
              record.recordData().get(2).asCurrency()));

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
