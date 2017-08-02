package se.montesmites.ekonomi.report;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.organization.Organization;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_NoRules_Rendering_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final Year year = Year.of(2012);

    private Organization organization;
    private CashflowDataFetcher fetcher;
    private CashflowReport report;

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
    }

    @Test
    public void render() throws Exception {
        File file = tempfolder.newFile();
        Files.copy(getClass().getResourceAsStream(
                "/se/montesmites/ekonomi/rapport/cashflowreport.txt"),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        List<String> exp = Files.readAllLines(file.toPath());
        List<String> act = report.render();
        assertEquals(exp.size(), act.size());
        for (int i = 0; i < exp.size(); i++) {
            String e = exp.get(i);
            String a = exp.get(i);
            assertEquals(i + "", e, a);
        }
    }
}
