package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.BinaryFile_VismaAdmin200;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;

public class ParserTest {

    private final static String PATH_TO_BINARY_FILES = "/se/montesmites/ekonomi/parser/vismaadmin200/v2015_0/";

    @Rule
    public TemporaryFolder tempfolder = new TemporaryFolder();

    @Test
    public void parseYears() throws Exception {
        copyTestFile(BinaryFile_2015_0.YEARS);
        Set<Year> exp = set(
                year("C", "2012", "2012-01-01", "2012-12-31"),
                year("D", "2013", "2013-01-01", "2013-12-31"),
                year("E", "2014", "2014-01-01", "2014-12-31"),
                year("F", "2015", "2015-01-01", "2015-12-31"));
        Set<Year> act = set(parse(BinaryFile_2015_0.YEARS));
        assertEquals(exp, act);
    }

    @Test
    public void parseAccounts() throws Exception {
        copyTestFile(BinaryFile_2015_0.ACCOUNTS);
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 525);
                put("D", (long) 526);
                put("E", (long) 536);
                put("F", (long) 590);
            }
        };
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.ACCOUNTS)
                .stream().collect(
                        Collectors.groupingBy(
                                account -> account.getAccountId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    @Test
    public void parseEvents() throws Exception {
        copyTestFile(BinaryFile_2015_0.EVENTS);
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
                .stream().collect(
                        Collectors.groupingBy(
                                event -> event.getEventId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }
    
    @Test
    public void parseEntries() throws Exception {
        copyTestFile(BinaryFile_2015_0.ENTRIES);
        final Map<String, Long> expCount = new HashMap<String, Long>() {{
            put("C", (long) 1306);
            put("D", (long) 1404);
            put("E", (long) 1344);
            put("F", (long) 218);
        }};
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.ENTRIES)
                .stream().collect(
                        Collectors.groupingBy(
                                entry -> entry.getEventId().getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }
    
    @Test
    public void parseBalances() throws Exception {
        copyTestFile(BinaryFile_2015_0.BALANCES);
        final Map<String, Long> expCount = new HashMap<String, Long>() {{
            put("C", (long) 33);
            put("D", (long) 34);
            put("E", (long) 33);
            put("F", (long) 27);
        }};
        final Map<String, Long> actCount = parse(BinaryFile_2015_0.BALANCES)
                .stream().collect(
                        Collectors.groupingBy(
                                balance -> balance.getYearId().getId(),
                                Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
    }

    private <T> List<T> parse(BinaryFile_VismaAdmin200<T> bf) {
        Parser p = new Parser(tempfolder.getRoot().toPath());
        return p.parse(bf);
    }

    private void copyTestFile(BinaryFile_2015_0<?> bf) throws IOException {
        final InputStream source = asStream(
                PATH_TO_BINARY_FILES + bf.getFileName());
        final File target = tempfolder.newFile(bf.getFileName());
        Files.copy(source, target.toPath(),
                new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
    }

    private InputStream asStream(String fullPath) {
        return getClass().getResourceAsStream(fullPath);
    }

    private Year year(String id, String year, String from, String to) {
        return new Year(new YearId(id), year, LocalDate.parse(from),
                LocalDate.parse(to));
    }

    private <T> Set<T> set(T... arr) {
        return set(Arrays.asList(arr));
    }

    private <T> Set<T> set(List<T> l) {
        return new HashSet<>(l);
    }
}
