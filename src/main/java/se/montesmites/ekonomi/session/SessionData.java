package se.montesmites.ekonomi.session;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;
import se.montesmites.ekonomi.db.FiscalYearData;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Data
public class SessionData {

  @With private final Optional<FiscalYearData> fiscalYear;

  public SessionData() {
    this(Optional.empty());
  }
}
