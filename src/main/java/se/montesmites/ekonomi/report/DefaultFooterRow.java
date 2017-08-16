package se.montesmites.ekonomi.report;

import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public class DefaultFooterRow implements FooterRow {

    private final Supplier<Stream<Row>> bodyRows;

    public DefaultFooterRow(Section parent) {
        this.bodyRows = () -> parent.streamBody();
    }

    public DefaultFooterRow(Supplier<Stream<Row>> bodyRows) {
        this.bodyRows = bodyRows;
    }

    @Override
    public Currency getMonthlyAmount(Column column) {
        return bodyRows.get()
                .map(row -> row.asRowWithAmounts().get())
                .map(row -> row.getMonthlyAmount(column))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
}
