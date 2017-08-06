package se.montesmites.ekonomi.report;

import java.util.Map;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.tuple.YearMonthAccountIdTuple;

public class AmountAggregate {
    private final Map<YearMonthAccountIdTuple, Currency> amountsByYearMonth;

    public AmountAggregate(Map<YearMonthAccountIdTuple, Currency> amountsByYearMonth) {
        this.amountsByYearMonth = amountsByYearMonth;
    }

    public Map<YearMonthAccountIdTuple, Currency> getAmountsByYearMonth() {
        return amountsByYearMonth;
    }
}
