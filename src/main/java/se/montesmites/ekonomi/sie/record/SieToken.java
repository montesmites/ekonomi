package se.montesmites.ekonomi.sie.record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import se.montesmites.ekonomi.model.Currency;

public class SieToken {

  public static SieToken of(String data) {
    return new SieToken(data);
  }

  private final String data;

  private SieToken(String data) {
    this.data = data;
  }

  public String asString() {
    return data;
  }

  public int asInt() {
    return Integer.parseInt(data);
  }

  public Currency asCurrency() {
    var parts = data.split("\\.");
    var integral = Long.parseLong(parts[0]) * 100;
    var decimal = parts.length == 1 ? 0 : Long.parseLong(parts[1]);
    return Currency.of(integral + decimal);
  }

  public LocalDate asDate() {
    return LocalDate.parse(data, DateTimeFormatter.BASIC_ISO_DATE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var sieToken = (SieToken) o;

    return data.equals(sieToken.data);
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }

  @Override
  public String toString() {
    return String.format("SieToken(%s)", data);
  }
}
