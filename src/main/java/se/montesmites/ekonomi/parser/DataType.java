package se.montesmites.ekonomi.parser;

import java.time.LocalDate;
import java.util.Optional;

interface DataType<T> {

    public final static class StringType implements DataType<String> {

        @Override
        public Optional<String> read(byte[] bytes, int start, int length) {
            return new ByteReader.StringReader().read(bytes, start, length);
        }
    }

    public final static class DateType implements DataType<LocalDate> {

        @Override
        public Optional<LocalDate> read(byte[] bytes, int start, int length) {
            return new ByteReader.DateReader().read(bytes, start, length);
        }
    }

    public Optional<T> read(byte[] bytes, int start, int length);
}
