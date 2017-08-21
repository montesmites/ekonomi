package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts extends Row {

    public Currency getMonthlyAmount(Column column);

    default Currency getYearlyTotal() {
        return Column.streamMonths()
                .map(this::getMonthlyAmount)
                .reduce(new Currency(0), (sum, term) -> sum.add(term));
    }
    
    default Currency getAverage() {
        double avg = Column.streamMonths()
                .map(this::getMonthlyAmount)
                .mapToLong(Currency::getAmount)
                .filter(amount -> amount != 0)
                .average()
                .orElse(0);
        return new Currency(Math.round(avg));
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
