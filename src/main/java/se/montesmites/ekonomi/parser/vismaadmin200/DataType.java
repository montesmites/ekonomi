package se.montesmites.ekonomi.parser.vismaadmin200;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;

public interface DataType<T> {

    @SuppressWarnings("Convert2Lambda")
    public final static DataType<byte[]> BYTE_ARRAY = new DataType<byte[]>() {

        @Override
        public Optional<byte[]> read(ByteChunk chunk, int start, int length) {
            if (chunk.getBytes().length >= start + length) {
                byte[] b = Arrays.copyOfRange(chunk.getBytes(), start,
                        start + length);
                return Optional.of(b);
            } else {
                return Optional.empty();
            }
        }
    };

    public final static DataType<String> STRING = new DataType<String>() {

        private final static String ENCODING = "Cp1252";

        @Override
        public Optional<String> read(ByteChunk chunk, int start, int length) {
            return BYTE_ARRAY.read(chunk, start, length)
                    .map(this::asString);
        }

        private String asString(byte[] bytes) {
            try {
                return new String(bytes, ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public final static DataType<LocalDate> DATE = new DataType<LocalDate>() {

        private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
                "yyyyMMdd");

        @Override
        public Optional<LocalDate> read(final ByteChunk chunk, int start, int length) {
            return STRING.read(chunk, start, length)
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

    @SuppressWarnings("Convert2Lambda")
    public final static DataType<Integer> INTEGER = new DataType<Integer>() {
        @Override
        public Optional<Integer> read(ByteChunk chunk, int start, int length) {
            return STRING.read(chunk, start, length)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt);
        }
    };

    @SuppressWarnings("Convert2Lambda")
    public final static DataType<Long> LONG = new DataType<Long>() {
        @Override
        public Optional<Long> read(ByteChunk chunk, int start, int length) {
            return STRING.read(chunk, start, length)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong);
        }
    };

    @SuppressWarnings("Convert2Lambda")
    public final static DataType<Currency> CURRENCY = new DataType<Currency>() {
        @Override
        public Optional<Currency> read(ByteChunk chunk, int start, int length) {
            return LONG.read(chunk, start, length).map(Currency::new);
        }
    };

    public Optional<T> read(ByteChunk chunk, int start, int length);
}
