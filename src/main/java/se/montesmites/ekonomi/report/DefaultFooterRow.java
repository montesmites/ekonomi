package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultFooterRow implements FooterRow {

    private final Supplier<Stream<Row>> bodyRows;

    public DefaultFooterRow(Section parent) {
        this.bodyRows = parent::streamBody;
    }

    public DefaultFooterRow(Supplier<Stream<Row>> bodyRows) {
        this.bodyRows = bodyRows;
    }

    @Override
    public Supplier<Stream<Month>> months() {
        return bodyRows.get()
                .findAny()
                .flatMap(Row::asRowWithAmounts)
                .map(RowWithAmounts::months)
                .orElse(Stream::empty);
    }

    @Override
    public Currency getMonthlyAmount(Column column) {
        return bodyRows.get()
                .map(row -> row.asRowWithAmounts().get())
                .map(row -> row.getMonthlyAmount(column))
                .reduce(new Currency(0), Currency::add);
    }
}
