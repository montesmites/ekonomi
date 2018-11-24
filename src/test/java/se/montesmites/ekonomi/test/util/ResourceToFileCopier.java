package se.montesmites.ekonomi.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0;

public class ResourceToFileCopier {

  private static final String PATH_TO_BINARY_FILES =
      "/se/montesmites/ekonomi/parser/vismaadmin200/v2015_0/";

  public void copyAll(Path path) {
    BinaryFile_2015_0.values().forEach(f -> copyTestFile(f, path));
  }

  private void copyTestFile(BinaryFile_2015_0<?> source, Path path) {
    try {
      final InputStream is = asStream(PATH_TO_BINARY_FILES + source.getFileName());
      Files.copy(is, Files.createFile(path.resolve(source.getFileName())), copyOptions());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private InputStream asStream(String fullPath) {
    return getClass().getResourceAsStream(fullPath);
  }

  private CopyOption[] copyOptions() {
    return new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
  }
}
