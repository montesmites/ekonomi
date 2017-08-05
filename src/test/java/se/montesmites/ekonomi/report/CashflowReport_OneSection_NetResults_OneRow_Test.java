package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.Column.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_OneSection_NetResults_OneRow_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final static String HEADER_TITLE = "Den löpande verksamheten";

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
        this.report = new CashflowReport(fetcher, year, () -> sections());
        this.section = report.streamSections().findFirst().get();
    }
    
    private Stream<Section> sections() {
        return Stream.of(new Section(HEADER_TITLE, fetcher, year));
    }
    
    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    public void sectionTitle() {
        final String exp = HEADER_TITLE.toUpperCase();
        final String act = section.getTitle().getText(DESCRIPTION);
        assertEquals(exp, act);
    }
}
