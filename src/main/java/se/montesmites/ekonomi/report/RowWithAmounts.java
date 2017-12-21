package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface RowWithAmounts extends Row {

    public Currency getMonthlyAmount(Column column);

    default Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyAmount)
                .reduce(new Currency(0), Currency::add);
    }
    
    default Currency getAverage() {
        double avg = months().get()
                .map(Column::valueOf)
                .map(this::getMonthlyAmount)
                .mapToLong(Currency::getAmount)
                .average()
                .orElse(0);
        return new Currency(Math.round(avg));
    }

    default Supplier<Stream<Month>> months() {
        return Stream::empty;
    }

    @Override
    default String formatMonth(Column column) {
        return getMonthlyAmount(column).format();
    }

    @Override
    default String formatTotal() {
        return getYearlyTotal().format();
    }

    @Override
    default String formatAverage() {
        return getAverage().format();
    }
}
