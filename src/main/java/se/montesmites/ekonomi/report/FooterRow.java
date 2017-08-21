package se.montesmites.ekonomi.report;

import java.util.Optional;

public interface FooterRow extends RowWithAmounts {

    @Override
    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.of(this);
    }
}
