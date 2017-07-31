package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;

public class CashflowReportBuilder {

    private final Organization organization;

    public CashflowReportBuilder(Organization organization) {
        this.organization = organization;
    }

    public CashflowReport build(Year year) {
        final List<Row> rows = yearMonths(year).flatMap(
                ym -> organization.getAccountIdAmountTuples(ym).get().stream()
                        .flatMap(t -> Stream.of(t.getAccountId())))
                .distinct()
                .sorted(comparing(AccountId::getId))
                .map(this::row)
                .collect(toList());
        CashflowReport report = new CashflowReport(rows);
        return report;
    }

    private Stream<YearMonth> yearMonths(Year year) {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }

    private Row row(AccountId accountId) {
        return new Row() {
            @Override
            public String getDescription() {
                return accountId.getId();
            }

            @Override
            public Optional<Currency> getAmount(YearMonth yearMonth) {
                Optional<Currency> amount
                        = organization.getAccountIdAmountMap(yearMonth)
                                .map(m -> m.get(accountId));
                return amount;
            }
        };
    }
}
