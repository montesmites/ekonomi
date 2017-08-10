package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.organization.Organization;
import static se.montesmites.ekonomi.report.Column.APRIL;
import static se.montesmites.ekonomi.report.Column.AUGUST;
import static se.montesmites.ekonomi.report.Column.DECEMBER;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.FEBRUARY;
import static se.montesmites.ekonomi.report.Column.JANUARY;
import static se.montesmites.ekonomi.report.Column.JULY;
import static se.montesmites.ekonomi.report.Column.JUNE;
import static se.montesmites.ekonomi.report.Column.MARCH;
import static se.montesmites.ekonomi.report.Column.MAY;
import static se.montesmites.ekonomi.report.Column.NOVEMBER;
import static se.montesmites.ekonomi.report.Column.OCTOBER;
import static se.montesmites.ekonomi.report.Column.SEPTEMBER;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_OneSection_TwoRows_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final static String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";
    private final static String BOKFORT_RESULTAT = "Bokfört resultat";
    private final static String KORTFRISTIGA_SKULDER = "Kortfristiga skulder";

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
        this.section = new Section(
                DEN_LOPANDE_VERKSAMHETEN,
                fetcher,
                year,
                () -> bodyRows());
    }

    private Stream<Section> sections() {
        return Stream.of(section);
    }

    private Stream<BodyRow> bodyRows() {
        return Stream.of(
                bodyRow(
                        filterAccounts("([3-7]\\d|8[1-8])\\d\\d"),
                        BOKFORT_RESULTAT),
                bodyRow(
                        filterAccounts("2[4-9]\\d\\d"),
                        KORTFRISTIGA_SKULDER));
    }

    private Supplier<Stream<AccountId>> filterAccounts(String regex) {
        final AccountFilter filter = new AccountFilterByRegex(regex);
        final Set<AccountId> accounts = filter.filter(
                fetcher.streamAccountIds(year)).collect(toSet());
        return () -> accounts.stream();
    }

    private BodyRow bodyRow(Supplier<Stream<AccountId>> accountIds, String description) {
        return new DefaultBodyRow(
                fetcher,
                accountIds,
                year,
                description);
    }

    @Test
    public void exactlyOneSection() {
        assertEquals(1, report.streamSections().count());
    }

    @Test
    public void sectionTitle() {
        final String exp = DEN_LOPANDE_VERKSAMHETEN.toUpperCase();
        final String act = section.getTitle().getText(DESCRIPTION);
        assertEquals(exp, act);
    }

    @Test
    public void body_rowDescription() {
        final List<String> exp
                = Arrays.asList(
                        BOKFORT_RESULTAT,
                        KORTFRISTIGA_SKULDER);
        final List<String> act
                = section.streamBodyRows()
                        .map(row -> row.getText(DESCRIPTION))
                        .collect(toList());
        assertEquals(exp.size(), act.size());
        assertEquals(exp.get(0), act.get(0));
        assertEquals(exp.get(1), act.get(1));
    }

    @Test
    public void testMonthlyAmountsForGroupedRow() {
        final List<Map<Column, Currency>> expList = Arrays.asList(
                new EnumMap<Column, Currency>(
                        Column.class) {
            {
                put(JANUARY, new Currency(2866947));
                put(FEBRUARY, new Currency(-6758901));
                put(MARCH, new Currency(-14611584));
                put(APRIL, new Currency(-2150417));
                put(MAY, new Currency(6833003));
                put(JUNE, new Currency(-30431649));
                put(JULY, new Currency(4877975));
                put(AUGUST, new Currency(-3810720));
                put(SEPTEMBER, new Currency(-4316052));
                put(OCTOBER, new Currency(123642));
                put(NOVEMBER, new Currency(-21238571));
                put(DECEMBER, new Currency(21533255));
            }
        },
                new EnumMap<Column, Currency>(
                        Column.class) {
            {
                put(JANUARY, new Currency(1387853));
                put(FEBRUARY, new Currency(-50442848));
                put(MARCH, new Currency(-63669735));
                put(APRIL, new Currency(137123271));
                put(MAY, new Currency(-9537958));
                put(JUNE, new Currency(288127));
                put(JULY, new Currency(10662616));
                put(AUGUST, new Currency(-14155331));
                put(SEPTEMBER, new Currency(12777139));
                put(OCTOBER, new Currency(-7434716));
                put(NOVEMBER, new Currency(6669553));
                put(DECEMBER, new Currency(-35070604));
            }
        });
        final List<Map<Column, Currency>> actList = section.streamBodyRows()
                .map(row -> Column.streamMonths()
                .map(col -> new AbstractMap.SimpleEntry<>(col,
                row.getMonthlyAmount(col)))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue)))
                .collect(toList());
        assertEquals(expList.size(), actList.size());
        for (int i = 0; i < expList.size(); i++) {
            final int ix = i;
            final Map<Column, Currency> exp = expList.get(i);
            final Map<Column, Currency> act = actList.get(i);
            final String fmt = "%s at %s: ";
            Column.streamMonths().forEach(column
                    -> assertEquals(
                            String.format(fmt, column.name(), ix),
                            exp.get(column),
                            act.get(column))
            );
        }
    }
}
