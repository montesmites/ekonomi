package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;
import static java.util.Comparator.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.Assert.*;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;

public class CashflowReport_NoAccountGroups_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);
    private YearId yearId;

    private Organization organization;
    private CashflowReportBuilder builder;
    private CashflowReport report;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.yearId = organization.getYear(year).get().getYearId();
        this.builder = new CashflowReportBuilder(this.organization);
        this.report = builder.build(year);
    }

    @Test
    public void cashflowReport_noAccountGroups_assertRows() {
        final List<String> expRowDescriptions = yearMonths().flatMap(
                ym -> organization.getAccountIdAmountTuples(ym).get().stream().map(
                        t -> t.getAccountId().getId())).distinct().sorted(
                        naturalOrder()).collect(toList());
        final List<String> actRowDescriptions
                = report.getRows().stream()
                        .map(Row::getDescription)
                        .collect(toList());
        assertEquals(expRowDescriptions, actRowDescriptions);
    }

    @Test
    public void assertYearMonthColumns() {
        report.getRows().stream()
                .forEach(row -> yearMonths()
                .forEach(ym -> assertAccountYearMonthAmount(row, ym)));
    }

    @Test
    public void assertBalances() {
        report.getRows().stream()
                .forEach(row -> assertBalance(row));
    }

    private void assertAccountYearMonthAmount(Row row, YearMonth yearMonth) {
        final Optional<Currency> exp
                = organization.getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(accountId(row)));
        final Optional<Currency> act = row.getAmount(yearMonth);
        String fmt = "%s (%s)";
        String msg = String.format(fmt, row.getDescription(), yearMonth);
        assertEquals(msg, exp, act);
    }

    private void assertBalance(Row row) {
        final Optional<Balance> exp
                = organization.getBalance(accountId(row));
        final Optional<Balance> act
                = row.getBalance();
        String fmt = "%s";
        String msg = String.format(fmt, row.getDescription());
        assertEquals(msg, exp, act);
    }

    private Stream<YearMonth> yearMonths() {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }

    private AccountId accountId(Row row) {
        return new AccountId(yearId, row.getDescription());
    }
}
