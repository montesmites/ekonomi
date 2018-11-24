package se.montesmites.ekonomi.report;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static se.montesmites.ekonomi.report.HeaderRow.HeaderType.HEADER_TYPE_SHORT_MONTHS;

public class TotallingSection implements Section {

    private final String title;
    private final List<Section> sections;

    public TotallingSection(String title, List<Section> sections) {
        this.title = title;
        this.sections = sections;
    }

    @Override
    public Header header() {
        return Header.of(() -> title).add((HeaderRow) () -> HEADER_TYPE_SHORT_MONTHS);
    }

    @Override
    public Stream<Row> streamFooter() {
        return Stream.<Row>builder().add(streamSectionRows()).add(new EmptyRow()).build();
    }

    private FooterRow streamSectionRows() {
        return () -> () -> sections.stream().flatMap(streamWrappedBody());
    }

    private Function<Section, Stream<? extends Row>> streamWrappedBody() {
        return (Section section) -> section.streamBody().flatMap(wrapRow(section));
    }

    private Function<Row, Stream<? extends Row>> wrapRow(Section section) {
        return row -> Stream.of(wrapSectionRow(section, row));
    }

    protected Row wrapSectionRow(Section section, Row row) {
        return row;
    }

    @Override
    public Stream<Row> streamBody() {
        return Stream.empty();
    }
}
