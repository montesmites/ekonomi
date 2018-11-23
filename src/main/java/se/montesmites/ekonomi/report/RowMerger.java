package se.montesmites.ekonomi.report;

import java.util.EnumMap;

class RowMerger {
    static RowMerger template(Row template) {
        var sources = new EnumMap<Column, Row>(Column.class);
        Column.stream().forEach(column -> sources.put(column, template));
        return new RowMerger(sources);
    }

    private final EnumMap<Column, Row> sources;

    private RowMerger(EnumMap<Column, Row> sources) {
        this.sources = sources;
    }

    RowMerger merge(Column column, Row row) {
        sources.put(column, row);
        return new RowMerger(sources);
    }

    Row asRow() {
        return column -> sources.get(column).format(column);
    }
}
