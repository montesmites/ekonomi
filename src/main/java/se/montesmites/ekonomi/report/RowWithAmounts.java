package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts extends Row {

    public Currency getMonthlyAmount(Column column);

    default Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyAmount)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }

    @Override
    default String formatMonth(Column column) {
        return getMonthlyAmount(column).format();
    }

    @Override
    default String formatTotal() {
        return getYearlyTotal().format();
    }
}
