package se.montesmites.ekonomi.sie.record.types;

import java.time.LocalDate;
import java.util.Optional;
import se.montesmites.ekonomi.sie.record.DefaultSieRecord;
import se.montesmites.ekonomi.sie.record.SieRecord;

public class TypeVER extends DefaultSieRecord {

  static TypeVER of(SieRecord record) {
    var series = record.getRecordData().get(0).asOptionalString();
    var eventId = record.getRecordData().get(1).asOptionalInt();
    var date = record.getRecordData().get(2).asDate();
    var description = record.getRecordData().get(3).asString();
    var registrationDate = record.getRecordData().get(4).asOptionalDate();
    var signature = record.getRecordData().get(5).asString();
    return new TypeVER(record, series, eventId, date, description, registrationDate, signature);
  }

  private final Optional<String> series;
  private final Optional<Integer> eventId;
  private final LocalDate date;
  private final String description;
  private final Optional<LocalDate> registrationDate;
  private final String signature;

  private TypeVER(
      SieRecord record,
      Optional<String> series,
      Optional<Integer> eventId,
      LocalDate date,
      String description,
      Optional<LocalDate> registrationDate,
      String signature) {
    super(record.getLine(), record.getLabel(), record.getRecordData(), record.getSubrecords());
    this.series = series;
    this.eventId = eventId;
    this.date = date;
    this.description = description;
    this.registrationDate = registrationDate;
    this.signature = signature;
  }

  public Optional<String> getSeries() {
    return series;
  }

  public Optional<Integer> getEventId() {
    return eventId;
  }

  public LocalDate getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public Optional<LocalDate> getRegistrationDate() {
    return registrationDate;
  }

  public String getSignature() {
    return signature;
  }
}
