package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.Column.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class DefaultRowWithAccountsWithNegatedAmountsTest {

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
    }

    @Test
    public void negateMonthlyAmount() {
        YearId yearId = organization.getYear(year).get().getYearId();
        Supplier<Stream<AccountId>> accountIds
                = () -> Stream.of(new AccountId(yearId, "1920"));
        final DefaultRowWithAccounts source
                = new DefaultRowWithAccounts(fetcher, accountIds, year, "");
        final DefaultRowWithAccountsWithNegatedAmounts negated
                = new DefaultRowWithAccountsWithNegatedAmounts(source);
        assertEquals(new Currency(-29543100), negated.getMonthlyAmount(JANUARY));
        assertEquals(new Currency(7546500), negated.getMonthlyAmount(FEBRUARY));
        assertEquals(new Currency(-47437100), negated.getMonthlyAmount(MARCH));
        assertEquals(new Currency(3653800), negated.getMonthlyAmount(APRIL));
        assertEquals(new Currency(3610100), negated.getMonthlyAmount(MAY));
        assertEquals(new Currency(-6197000), negated.getMonthlyAmount(JUNE));
        assertEquals(new Currency(35616342), negated.getMonthlyAmount(JULY));
        assertEquals(new Currency(-28806200), negated.getMonthlyAmount(AUGUST));
        assertEquals(new Currency(11695600), negated.getMonthlyAmount(SEPTEMBER));
        assertEquals(new Currency(16937600), negated.getMonthlyAmount(OCTOBER));
        assertEquals(new Currency(-23696499), negated.getMonthlyAmount(NOVEMBER));
        assertEquals(new Currency(-5269900), negated.getMonthlyAmount(DECEMBER));
    }
}
