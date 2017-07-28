package se.montesmites.ekonomi.test.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import org.junit.rules.TemporaryFolder;
import se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0;

public class ResourceToFileCopier {

    private final static String PATH_TO_BINARY_FILES = "/se/montesmites/ekonomi/parser/vismaadmin200/v2015_0/";

    public void copyAll(TemporaryFolder tempfolder) {
        List<BinaryFile_2015_0> files = Arrays.asList(
                BinaryFile_2015_0.ACCOUNTS,
                BinaryFile_2015_0.BALANCES,
                BinaryFile_2015_0.ENTRIES,
                BinaryFile_2015_0.EVENTS,
                BinaryFile_2015_0.YEARS);
        files.stream().forEach(f -> copyTestFile(f, tempfolder));
    }

    public void copyTestFile(BinaryFile_2015_0<?> source, TemporaryFolder target) {
        try {
            final InputStream is = asStream(
                    PATH_TO_BINARY_FILES + source.getFileName());
            final File file = target.newFile(source.getFileName());
            Files.copy(is, file.toPath(), copyOptions());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream asStream(String fullPath) {
        return getClass().getResourceAsStream(fullPath);
    }

    private CopyOption[] copyOptions() {
        return new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
    }
}
