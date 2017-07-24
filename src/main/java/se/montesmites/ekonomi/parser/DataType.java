package se.montesmites.ekonomi.parser;

import java.time.LocalDate;

interface DataType<T> {

    public final static class StringType implements DataType<String> {

    }

    public final static class DateType implements DataType<LocalDate> {

    }
}
