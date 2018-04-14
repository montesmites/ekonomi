package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.AccountId;

import java.util.stream.Stream;

public interface AccountFilter {
    Stream<AccountId> filter(Stream<AccountId> accountIds);
}
