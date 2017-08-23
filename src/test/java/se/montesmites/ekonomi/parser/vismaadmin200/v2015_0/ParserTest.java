package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    @ClassRule
    public static TemporaryFolder tempfolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws Exception {
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(tempfolder);
    }

    @Test
    public void parseYears() throws Exception {
        Set<Year> exp = set(
                year("C", 2012, "2012-01-01", "2012-12-31"),
                year("D", 2013, "2013-01-01", "2013-12-31"),
                year("E", 2014, "2014-01-01", "2014-12-31"),
                year("F", 2015, "2015-01-01", "2015-12-31"));
        Set<Year> act = set(parse(BinaryFile_2015_0.YEARS));
        assertEquals(exp, act);
    }

    @Test
    public void parseAccounts() throws Exception {
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 525);
                put("D", (long) 526);
                put("E", (long) 536);
                put("F", (long) 590);
            }
        };
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.ACCOUNTS)
                .collect(
                        Collectors.groupingBy(
                                account -> account.getAccountId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    public void parseEvents() throws Exception {
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("A", (long) 1);
                put("C", (long) 272);
                put("D", (long) 310);
                put("E", (long) 295);
                put("F", (long) 62);
            }
        };
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.EVENTS)
                .collect(
                        Collectors.groupingBy(
                                event -> event.getEventId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    public void parseEntries() throws Exception {
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 1313);
                put("D", (long) 1404);
                put("E", (long) 1344);
                put("F", (long) 218);
            }
        };
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.ENTRIES)
                .collect(
                        Collectors.groupingBy(
                                entry -> entry.getEventId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    public void parseBalances() throws Exception {
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 33);
                put("D", (long) 34);
                put("E", (long) 33);
                put("F", (long) 27);
            }
        };
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.BALANCES)
                .collect(
                        Collectors.groupingBy(
                                balance -> balance.getAccountId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    private <T> Stream<T> parse(BinaryFile_VismaAdmin200<T> bf) {
        Parser p = new Parser(tempfolder.getRoot().toPath());
        return p.parse(bf);
    }

    private Year year(String id, int year, String from, String to) {
        return new Year(new YearId(id), java.time.Year.of(year),
                LocalDate.parse(from), LocalDate.parse(to));
    }

    @SafeVarargs
    private final <T> Set<T> set(T... arr) {
        return set(Arrays.stream(arr));
    }

    private <T> Set<T> set(Stream<T> l) {
        return l.collect(toSet());
    }
}
