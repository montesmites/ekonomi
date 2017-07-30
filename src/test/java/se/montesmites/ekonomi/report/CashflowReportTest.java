package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
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
import static org.junit.Assert.*;

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
    public void cashflowReportCalendarYear_noRowModel_assertRows() {
        CashflowReportBuilder builder = new CashflowReportBuilder(
                this.organization);
        CashflowReport report = builder.build(java.time.Year.of(2012));
        final List<String> allAccounts2012 = Arrays.stream(Month.values())
                .flatMap(
                        m -> organization.getAccountIdAmountTuples(
                                YearMonth.of(2012, m)).get()
                                .stream().map(
                                        t -> t.getAccountId().getId()))
                .distinct().sorted(naturalOrder()).collect(toList());
        assertEquals(allAccounts2012.size(), report.getRowCount());
        allAccounts2012.stream().forEach(
                acc -> assertTrue(
                        acc,
                        report.getRows().stream().anyMatch(
                                row -> row.getDescription().equals(acc))));
    }
}
