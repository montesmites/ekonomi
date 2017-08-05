package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.List;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.Column.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_OneSection_NetResults_OneRow_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final static String HEADER_TITLE = "Den löpande verksamheten";
    private final static String ROW_DESCRIPTION = "Kassaflöde från den löpande verksamheten";

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
        this.section = new Section(HEADER_TITLE, fetcher, year, () -> bodyRows());
    }

    private Stream<Section> sections() {
        return Stream.of(section);
    }

    private Stream<BodyRow> bodyRows() {
        return bodyRow(filterAccounts());
    }

    private Stream<AccountId> filterAccounts() {
        final String regex = "([3-7]\\d|8[1-8])\\d\\d";
        final AccountFilter filter = new AccountFilterByRegex(regex);
        final Stream<AccountId> accounts = filter.filter(
                fetcher.streamAccountIds(year));
        return accounts;
    }

    private Stream<BodyRow> bodyRow(Stream<AccountId> accountIds) {
        return Stream.of(
                new DefaultBodyRow(
                        fetcher,
                        () -> accountIds,
                        year,
                        ROW_DESCRIPTION));
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

    @Test
    public void body_rowDescription() {
        final String exp = ROW_DESCRIPTION;
        final List<String> act
                = section.streamBodyRows()
                        .map(row -> row.getText(DESCRIPTION))
                        .collect(toList());
        assertEquals(1, act.size());
        assertEquals(exp, act.get(0));
    }
}
