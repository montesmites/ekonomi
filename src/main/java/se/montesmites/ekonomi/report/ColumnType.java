package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.Optional;
import java.util.function.Function;

public enum ColumnType {
    DESCRIPTION(__ -> Optional.empty()),
    MONTH(c -> Optional.of(Month.valueOf(c.name()))),
    TOTAL(__ -> Optional.empty()),
    AVERAGE(__ -> Optional.empty());

    private final Function<Column, Optional<Month>> asMonthFunction;

    ColumnType(Function<Column, Optional<Month>> asMonthFunction) {
        this.asMonthFunction = asMonthFunction;
    }

    public final Optional<Month> asMonth(Column column) {
        return asMonthFunction.apply(column);
    }
}
