package se.montesmites.ekonomi.report;

import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;

public interface AccountFilter {

  Stream<AccountId> filter(Stream<AccountId> accountIds);
}
