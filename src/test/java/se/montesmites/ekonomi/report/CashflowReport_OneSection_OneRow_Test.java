package se.montesmites.ekonomi.report;

import java.time.Year;
import java.util.AbstractMap.SimpleEntry;
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
import static se.montesmites.ekonomi.report.Column.*;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class CashflowReport_OneSection_OneRow_Test {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    private final static String DEN_LOPANDE_VERKSAMHETEN = "Den löpande verksamheten";
    private final static String BOKFORT_RESULTAT = "Bokfört resultat";

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
        this.section = new Section(DEN_LOPANDE_VERKSAMHETEN, fetcher, year, () -> bodyRows());
    }

    private Stream<Section> sections() {
        return Stream.of(section);
    }

    private Stream<BodyRow> bodyRows() {
        return Stream.of(
                bodyRow(
                        filterAccounts("([3-7]\\d|8[1-8])\\d\\d"),
                        BOKFORT_RESULTAT));
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
        final String exp = BOKFORT_RESULTAT;
        final List<String> act
                = section.streamBodyRows()
                        .map(row -> row.getText(DESCRIPTION))
                        .collect(toList());
        assertEquals(1, act.size());
        assertEquals(exp, act.get(0));
    }

    @Test
    public void testMonthlyAmountsForGroupedRow() {
        final Map<Column, Currency> exp = new EnumMap<Column, Currency>(
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
        };
        final List<Map<Column, Currency>> actList = section.streamBodyRows()
                .map(row -> Column.streamMonths()
                .map(col -> new SimpleEntry<>(col, row.getMonthlyAmount(col)))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue)))
                .collect(toList());
        final Map<Column, Currency> act = actList.get(0);
        assertEquals(1, actList.size());
        Column.streamMonths().forEach(column
                -> assertEquals(column.name(), exp.get(column), act.get(column))
        );
    }
}
