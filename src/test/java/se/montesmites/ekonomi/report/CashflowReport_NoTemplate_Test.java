package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_NoTemplate_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;
    private Section section;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new CashflowDataFetcher(this.organization);
        this.report = new CashflowReport(fetcher, year);
        this.section = report.sectionStream().findFirst().get();
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.sectionStream().count());
    }

    @Test
    public void columnLabels() {
        List<String> expColumnLabels = Arrays.asList("Description", "Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec", "Total");
        List<String> actColumnLabels = report.columnStream().map(
                Column::getLabel).collect(toList());
        assertEquals(expColumnLabels, actColumnLabels);
    }
    
    @Test
    public void body_rowCount() {
        assertEquals(fetcher.streamAccountIds(year).count(), section.bodyStream().count());
    }
}
