package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.List;
import static se.montesmites.ekonomi.parser.vismaadmin200.DataType.*;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;
import static se.montesmites.ekonomi.parser.vismaadmin200.v2015_0.BinaryFile_2015_0_DefinitionUtil.*;

enum BinaryFile_2015_0_Definition {
    YEARS("BOKFAAR.DBF", 513, 89, fields(
            field("status", STRING, 0, 1),
            field("yearid", STRING, 1, 1),
            field("year", STRING, 2, 4),
            field("from", DATE, 2, 8),
            field("to", DATE, 10, 8))
    );

    private final String fileName;
    private final int start;
    private final int length;
    private final List<Field<?>> fields;

    private BinaryFile_2015_0_Definition(String fileName, int start, int length, List<Field<?>> fields) {
        this.fileName = fileName;
        this.start = start;
        this.length = length;
        this.fields = fields;
    }

    public String getFileName() {
        return fileName;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
    
    public List<Field<?>> getFields() {
        return fields;
    }
}
