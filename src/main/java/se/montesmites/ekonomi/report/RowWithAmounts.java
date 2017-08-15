package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts {

    public Currency getMonthlyAmount(Column column);

    default Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyAmount)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
}
