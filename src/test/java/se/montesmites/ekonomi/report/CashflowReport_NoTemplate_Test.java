package se.montesmites.ekonomi.report;
import static org.junit.Assert.*;
import java.time.Year;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_NoTemplate_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private AccountAmountFetcher fetcher;
    private CashflowReport report;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Before
    public void before() throws Exception {
        this.organization = Organization.fromPath(tempfolder.getRoot().toPath());
        this.fetcher = new AccountAmountFetcher(this.organization);
        this.report = new CashflowReport(fetcher);
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.sectionStream().count());
    }
}
