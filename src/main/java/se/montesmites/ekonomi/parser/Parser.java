package se.montesmites.ekonomi.parser;

import java.nio.file.Path;
import java.util.List;

class Parser {

    private final Path path;

    Parser(Path path) {
        this.path = path;
    }

    public <T> List<T> parse(BinaryFile_2015_0<T> bf) {
        return bf.parse(path);
    }
}
