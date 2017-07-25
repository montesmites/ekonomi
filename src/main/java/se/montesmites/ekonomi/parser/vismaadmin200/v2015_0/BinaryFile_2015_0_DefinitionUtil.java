package se.montesmites.ekonomi.parser.vismaadmin200.v2015_0;

import java.util.Arrays;
import java.util.List;
import se.montesmites.ekonomi.parser.vismaadmin200.DataType;
import se.montesmites.ekonomi.parser.vismaadmin200.Field;

class BinaryFile_2015_0_DefinitionUtil {
    final static List<Field<?>> fields(Field<?>... fields) {
        return Arrays.asList(fields);
    }
    
    final static Field<?> field(String id, DataType<?> datatype, int start, int length) {
        return Field.define(id, datatype, start, length);
    }    
}
