package se.montesmites.ekonomi.parser.vismaadmin200;

import static java.util.Arrays.copyOfRange;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;

public interface DataType<T> {

  DataType<byte[]> BYTE_ARRAY =
      (chunk, start, length) -> {
        if (chunk.getBytes().length >= start + length) {
          return Optional.of(copyOfRange(chunk.getBytes(), start, start + length));
        } else {
          return Optional.empty();
        }
      };

  DataType<String> STRING =
      new DataType<>() {

        private static final String ENCODING = "Cp1252";

        @Override
        public Optional<String> read(ByteChunk chunk, int start, int length) {
          return BYTE_ARRAY.read(chunk, start, length).map(this::asString);
        }

        private String asString(byte[] bytes) {
          try {
            return new String(bytes, ENCODING);
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
        }
      };

  DataType<LocalDate> DATE =
      new DataType<>() {

        private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

        @Override
        public Optional<LocalDate> read(final ByteChunk chunk, int start, int length) {
          return STRING
              .read(chunk, start, length)
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(s -> parseDate(chunk, s));
        }

        private LocalDate parseDate(ByteChunk chunk, String str) {
          try {
            return LocalDate.parse(str, DATE_FORMAT);
          } catch (DateTimeParseException e) {
            throw new RuntimeException(chunk.toString(), e);
          }
        }
      };

  DataType<Integer> INTEGER =
      (chunk, start, length) ->
          STRING
              .read(chunk, start, length)
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(Integer::parseInt);

  DataType<Long> LONG =
      (chunk, start, length) ->
          STRING
              .read(chunk, start, length)
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(Long::parseLong);

  DataType<Currency> CURRENCY =
      (chunk, start, length) -> LONG.read(chunk, start, length).map(Currency::of);

  Optional<T> read(ByteChunk chunk, int start, int length);
}
