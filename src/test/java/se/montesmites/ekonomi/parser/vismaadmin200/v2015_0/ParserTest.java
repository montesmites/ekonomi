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
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;

public class ParserTest {

    private final static String PATH_TO_BINARY_FILES = "/se/montesmites/ekonomi/parser/vismaadmin200/v2015_0/";

    @Rule
    public TemporaryFolder tempfolder = new TemporaryFolder();

    @Test
    public void parseYears() throws Exception {
        copyTestFile(BinaryFile_2015_0.YEARS);
        Parser p = new Parser(tempfolder.getRoot().toPath());
        Set<Year> exp = set(
                year("C", "2012", "2012-01-01", "2012-12-31"),
                year("D", "2013", "2013-01-01", "2013-12-31"),
                year("E", "2014", "2014-01-01", "2014-12-31"),
                year("F", "2015", "2015-01-01", "2015-12-31"));
        Set<Year> act = set(p.parse(BinaryFile_2015_0.YEARS));
        assertEquals(exp, act);
    }

    @Test
    public void parseAccounts() throws Exception {
        copyTestFile(BinaryFile_2015_0.ACCOUNTS);
        Parser p = new Parser(tempfolder.getRoot().toPath());
        final Map<String, Long> expCount = new HashMap<String, Long>() {
            {
                put("C", (long) 525);
                put("D", (long) 526);
                put("E", (long) 536);
                put("F", (long) 590);
            }
        };
        final Map<String, Long> actCount = p.parse(BinaryFile_2015_0.ACCOUNTS).stream().collect(
                Collectors.groupingBy(
                        (Account account) -> account.getAccountId().getYearId().getId(),
                        Collectors.counting()));
        assertEquals(expCount.entrySet(), actCount.entrySet());
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
