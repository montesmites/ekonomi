package se.montesmites.ekonomi.report;

import java.util.stream.Stream;

class SectionBuilder {
    private final Stream.Builder<Row> rows;

    SectionBuilder() {
        this.rows = Stream.builder();
    }

    public SectionBuilder addTitleRow(TitleRow titleRow) {
        this.rows.add(titleRow);
        return this;
    }

    public SectionBuilder addHeaderRow(HeaderRow headerRow) {
        this.rows.add(headerRow);
        return this;
    }

    public SectionBuilder addFooterRow(FooterRow footerRow) {
        this.rows.add(footerRow);
        return this;
    }

    public SectionBuilder addRow(Row row) {
        this.rows.add(row);
        return this;
    }

    public SectionBuilder addAll(Stream<Row> rows) {
        rows.forEach(this.rows::add);
        return this;
    }

    public Section build() {
        return Section.of(rows::build);
    }
}
