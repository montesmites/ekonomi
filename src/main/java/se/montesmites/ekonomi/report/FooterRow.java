package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public class FooterRow implements Row, RowWithAmounts {

    private final Supplier<Stream<BodyRow>> bodyRows;

    public FooterRow(Section parent) {
        this.bodyRows = () -> parent.streamBodyRows();
    }
    
    public FooterRow(Supplier<Stream<BodyRow>> bodyRows) {
        this.bodyRows = bodyRows;
    }

    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return "Total";
            case TOTAL:
                return getYearlyTotal().format();
            default:
                return getMonthlyAmount(column).format();
        }
    }
    
    @Override
    public Currency getMonthlyAmount(Column column) {
        return bodyRows.get()
                .map(row -> row.getMonthlyAmount(column))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    @Override
    public Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }
}
