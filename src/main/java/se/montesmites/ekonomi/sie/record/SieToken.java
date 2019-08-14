package se.montesmites.ekonomi.sie.record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;

public class SieToken {

  public static class EmptySieToken extends SieToken {

    private EmptySieToken() {
      super("");
    }

    @Override
    public Optional<String> asOptionalString() {
      return Optional.empty();
    }

    @Override
    public Optional<Integer> asOptionalInt() {
      return Optional.empty();
    }

    @Override
    public Optional<Currency> asOptionalCurrency() {
      return Optional.empty();
    }

    @Override
    public Optional<LocalDate> asOptionalDate() {
      return Optional.empty();
    }
  }

  public static SieToken empty() {
    return new EmptySieToken();
  }

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

  public Optional<String> asOptionalString() {
    return Optional.of(asString());
  }

  public int asInt() {
    return Integer.parseInt(data);
  }

  public Optional<Integer> asOptionalInt() {
    return Optional.of(asInt());
  }

  public Currency asCurrency() {
    var parts = data.split("\\.");
    var integral = Long.parseLong(parts[0]) * 100;
    var decimal = parts.length == 1 ? 0 : Long.parseLong(parts[1]);
    return Currency.of(integral + decimal);
  }

  public Optional<Currency> asOptionalCurrency() {
    return Optional.of(asCurrency());
  }

  public LocalDate asDate() {
    return LocalDate.parse(data, DateTimeFormatter.BASIC_ISO_DATE);
  }

  public Optional<LocalDate> asOptionalDate() {
    return Optional.of(asDate());
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
