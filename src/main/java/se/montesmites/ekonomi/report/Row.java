package se.montesmites.ekonomi.report;

import java.time.YearMonth;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;

public interface Row {
    public String getDescription();
    public Optional<Currency> getAmount(YearMonth month);
}
