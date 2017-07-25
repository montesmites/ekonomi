package se.montesmites.ekonomi.parser;

import java.nio.file.Path;
import java.util.List;

public interface BinaryFile_VismaAdmin200<T> {

    public List<T> parse(Path path);
}
