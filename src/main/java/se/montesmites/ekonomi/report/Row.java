package se.montesmites.ekonomi.report;

import java.util.Optional;

public interface Row {
    public String getText(Column column);
    
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.empty();
    }
}
