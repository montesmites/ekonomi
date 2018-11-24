package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

import java.time.Month;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface FooterRow extends RowWithAmounts {
    Supplier<Stream<Row>> getBodyRows();

    @Override
    default Supplier<Stream<Month>> months() {
        return getBodyRows()
                .get()
                .findAny()
                .flatMap(Row::asRowWithAmounts)
                .map(RowWithAmounts::months)
                .orElse(Stream::empty);
    }

    @Override
    default Currency getMonthlyAmount(Column column) {
        return getBodyRows()
                .get()
                .map(row -> row.asRowWithAmounts().orElseThrow())
                .map(row -> row.getMonthlyAmount(column))
                .reduce(new Currency(0), Currency::add);
    }

    @Override
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }
}
