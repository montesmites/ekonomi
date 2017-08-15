package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts {

    public Currency getMonthlyAmount(Column column);

    public Currency getYearlyTotal();
}
