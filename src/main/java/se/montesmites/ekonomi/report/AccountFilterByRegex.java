package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.AccountId;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AccountFilterByRegex implements AccountFilter {
    private final Pattern pattern;

    public AccountFilterByRegex(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public Stream<AccountId> filter(Stream<AccountId> accountIds) {
        return accountIds.filter(a -> pattern.matcher(a.getId()).matches()).distinct();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
