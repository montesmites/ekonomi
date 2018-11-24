package testdata;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import se.montesmites.ekonomi.organization.OrganizationBuilder;
import se.montesmites.ekonomi.parser.vismaadmin200.Parser;
import se.montesmites.ekonomi.test.util.ResourceToFileCopier;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DefaultTestDataExtension
        implements BeforeAllCallback, TestInstancePostProcessor, AfterAllCallback {
    private static final String PATH_TO_BINARY_FILES =
            "/se/montesmites/ekonomi/parser/vismaadmin200/v2015_0/";

    private File tempfolder;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        tempfolder = Files.createTempDirectory("defaultTestData").toFile();
        ResourceToFileCopier copier = new ResourceToFileCopier();
        copier.copyAll(Paths.get(tempfolder.toURI()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (tempfolder != null) {
            recursiveDelete(tempfolder);
        }
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        file.delete();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        try {
            for (Field field : testInstance.getClass().getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
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
