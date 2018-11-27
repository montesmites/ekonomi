package testdata;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

public class DefaultTestDataExtension
    implements BeforeAllCallback, TestInstancePostProcessor, AfterAllCallback {

  private File tempfolder;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    tempfolder = Files.createTempDirectory("defaultTestData").toFile();
    var fileCopier = new ResourceToFileCopier();
    fileCopier.copyAll(Paths.get(tempfolder.toURI()));
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    if (tempfolder != null) {
      recursiveDelete(tempfolder);
    }
  }

  private void recursiveDelete(File directory) {
    var files = directory.listFiles();
    if (files != null) {
      for (var file : files) {
        recursiveDelete(file);
      }
    }
    directory.delete();
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    try {
      for (var field : testInstance.getClass().getDeclaredFields()) {
        for (var annotation : field.getAnnotations()) {
          if (annotation.annotationType() == OrganizationInjector.class) {
            field.setAccessible(true);
            field.set(testInstance, new OrganizationBuilder(tempfolder.toPath()).build());
          } else if (annotation.annotationType() == ParserInjector.class) {
            field.setAccessible(true);
            field.set(testInstance, new Parser(tempfolder.toPath()));
          } else if (annotation.annotationType() == PathToBinaryFiles.class) {
            field.setAccessible(true);
            field.set(testInstance, tempfolder.toPath());
          }
        }
      }
    } catch (IllegalAccessException iae) {
      throw new RuntimeException(iae);
    }
  }
}
