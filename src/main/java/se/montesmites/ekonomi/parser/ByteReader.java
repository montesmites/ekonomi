package se.montesmites.ekonomi.parser;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

interface ByteReader<T> {

    public final static class BytesReader implements ByteReader<byte[]> {

        @Override
        public Optional<byte[]> read(byte[] bytes, int start, int length) {
            if (bytes.length >= start + length) {
                byte[] b = Arrays.copyOfRange(bytes, start, start + length);
                return Optional.of(b);
            } else {
                return Optional.empty();
            }
        }
    }

    public final static class StringReader implements ByteReader<String> {

        private final static String ENCODING = "Cp1252";

        @Override
        public Optional<String> read(byte[] bytes, int start, int length) {
            return new BytesReader().read(bytes, start, length).map(
                    this::asString);
        }

        private String asString(byte[] bytes) {
            try {
                return new String(bytes, ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final static class DateReader implements ByteReader<LocalDate> {

        private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
                "yyyyMMdd");

        @Override
        public Optional<LocalDate> read(byte[] bytes, int start, int length) {
            return new StringReader().read(bytes, start, length).map(
                    s -> LocalDate.parse(s.trim(), DATE_FORMAT));
        }
    }

    public Optional<T> read(byte[] bytes, int start, int length);
}
