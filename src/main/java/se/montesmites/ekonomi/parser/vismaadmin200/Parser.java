package se.montesmites.ekonomi.parser.vismaadmin200;

import java.nio.file.Path;
import java.util.List;

public class Parser {

    private final Path path;

    public Parser(Path path) {
        this.path = path;
    }

    public <T> List<T> parse(BinaryFile_VismaAdmin200<T> binaryFile) {
        return binaryFile.parse(path);
    }
}
