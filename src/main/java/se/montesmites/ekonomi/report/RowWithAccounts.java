package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public interface RowWithAccounts extends Row, RowWithAmounts {
    
    public String getDescription();
    
    public Supplier<Stream<AccountId>> getAccountIds();
    
    @Override
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }
    
    @Override
    default String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return getDescription();
            case TOTAL:
                return getYearlyTotal().format();
            default:
                return getMonthlyAmount(column).format();
        }
    }
}
