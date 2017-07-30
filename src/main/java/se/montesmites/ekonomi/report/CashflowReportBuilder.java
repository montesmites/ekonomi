package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import se.montesmites.ekonomi.organization.Organization;

public class CashflowReportBuilder {

    private final Organization organization;

    public CashflowReportBuilder(Organization organization) {
        this.organization = organization;
    }

    public CashflowReport build(Year year) {
        final List<Row> rows = Arrays.stream(Month.values()).flatMap(
                m -> organization.getAccountIdAmountTuples(YearMonth.of(2012, m)).get().stream().flatMap(
                        t -> Stream.of(t.getAccountId().getId()))).distinct().sorted(
                        naturalOrder()).map(this::row).collect(toList());
        CashflowReport report = new CashflowReport(rows);
        return report;
    }

    private Row row(String account) {
        return new Row() {
            @Override
            public String getDescription() {
                return account;
            }
        };
    }
}
