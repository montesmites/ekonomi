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
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;

public class CashflowReportTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private Organization organization;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
    }

    @Test
    public void cashflowReportCalendarYear_noRowModel() {
        CashflowReportBuilder builder = new CashflowReportBuilder(
                this.organization);
        final Year year = Year.of(2012);
        CashflowReport report = builder.build(year);
        final List<String> expRowDescriptions = yearMonths(year).flatMap(
                ym -> organization.getAccountIdAmountTuples(ym).get().stream().map(
                        t -> t.getAccountId().getId())).distinct().sorted(
                        naturalOrder()).collect(toList());
        final List<String> actRowDescriptions
                = report.getRows().stream()
                        .map(Row::getDescription)
                        .collect(toList());
        assertEquals(expRowDescriptions, actRowDescriptions);
        assertColumns(report, year);
    }

    private void assertColumns(CashflowReport report, Year year) {
        report.getRows().stream()
                .forEach(row
                        -> yearMonths(year)
                        .forEach(ym
                                -> assertAccountYearMonthAmount(row, ym)));
    }

    private void assertAccountYearMonthAmount(Row row, YearMonth ym) {
        final Optional<Currency> exp = getAccountMonthAmount(row, ym);
        final Optional<Currency> act = row.getAmount(ym);
        String fmt = "%s (%s)";
        String msg = String.format(fmt, row.getDescription(), ym);
        assertEquals(msg, exp, act);
    }

    private Optional<Currency> getAccountMonthAmount(Row row, YearMonth yearMonth) {
        final YearId yearId = organization.getYear(
                Year.of(yearMonth.getYear())).get().getYearId();
        final AccountId accountId = new AccountId(yearId, row.getDescription());
        final Optional<Currency> amount
                = organization.getAccountIdAmountMap(yearMonth)
                        .map(m -> m.get(accountId));
        return amount;
    }

    private Stream<YearMonth> yearMonths(Year year) {
        return stream(Month.values()).map(m -> YearMonth.of(year.getValue(), m));
    }
}
