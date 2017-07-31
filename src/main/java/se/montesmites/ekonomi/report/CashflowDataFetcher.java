package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import java.util.Optional;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;

public class CashflowDataFetcher {

    private final Organization organization;

    public CashflowDataFetcher(Organization organization) {
        this.organization = organization;
    }

    public Stream<AccountId> streamAccountIds(Year year) {
        return yearMonths(year).flatMap(
                ym -> organization.getAccountIdAmountTuples(ym).get().stream()
                        .flatMap(t -> Stream.of(t.getAccountId())))
                .distinct()
                .sorted(comparing(AccountId::getId));
    }

    public Optional<Balance> fetchBalance(AccountId accountId) {
        return organization.getBalance(accountId);
    }

    public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        Optional<Currency> amount
                = organization.getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(accountId));
        return amount;
    }

    private Stream<YearMonth> yearMonths(Year year) {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }
}
