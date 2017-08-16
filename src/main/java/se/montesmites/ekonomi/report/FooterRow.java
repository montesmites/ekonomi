package se.montesmites.ekonomi.report;

import java.util.Optional;

public interface FooterRow extends Row, RowWithAmounts {
    
    default String getDescription() {
        return "";
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

    @Override
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }
}
