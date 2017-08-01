package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public class FooterRow implements Row {

    private final Section parent;
    private final java.time.Year year;

    public FooterRow(Section parent, java.time.Year year) {
        this.parent = parent;
        this.year = year;
    }

    @Override
    public String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return "Total";
            case TOTAL:
                return getYearlyTotal().format();
            default:
                return getMonthlyTotal(column).format();
        }
    }

    public Currency getMonthlyTotal(Column column) {
        return parent.streamBodyRows()
                .map(row
                        -> row.getMonthlyAmount(column)
                        .orElse(new Currency(0)))
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    public Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyTotal)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
}
