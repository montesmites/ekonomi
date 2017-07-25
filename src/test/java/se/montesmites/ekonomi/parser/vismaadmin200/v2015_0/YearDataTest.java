package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;

public class YearDataTest {

    private final static String PATH_TO_BINARY_FILES = "/se/montesmites/ekonomi/parser/files/";

    @Rule
    public TemporaryFolder tempfolder = new TemporaryFolder();

    @Test
    public void testContainsYears() throws Exception {
        final InputStream source = asStream(
                PATH_TO_BINARY_FILES + BinaryFile_2015_0.YEARS.getFileName());
        final File target = tempfolder.newFile(
                BinaryFile_2015_0.YEARS.getFileName());
        Files.copy(source, target.toPath(),
                new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
        Parser p = new Parser(tempfolder.getRoot().toPath());
        List<Year> years = p.parse(BinaryFile_2015_0.YEARS);
        assertEquals(4, years.size());
    }

    @Test
    public void test() {
        assertTrue(true);
    }

    private InputStream asStream(String fullPath) {
        return getClass().getResourceAsStream(fullPath);
    }
}
