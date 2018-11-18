package se.montesmites.ekonomi.report;

import java.util.List;
import java.util.stream.Stream;

public class TotallingSection implements Section {

    private final String title;
    private final List<Section> sections;

    public TotallingSection(String title, List<Section> sections) {
        this.title = title;
        this.sections = sections;
    }

    @Override
    public Stream<Row> streamTitle() {
        return Stream.of((TitleRow) () -> title);
    }

    @Override
    public Stream<Row> streamHeader() {
        return Stream.of(new HeaderRow() {
        });
    }

    @Override
    public Stream<Row> streamFooter() {
        return Stream.of(
                new DefaultFooterRow(
                        ()
                        -> sections.stream()
                                .flatMap(
                                        (Section section) -> section.streamBody()
                                                .flatMap(row
                                                        -> Stream.of(
                                                        wrapSectionRow(
                                                                section,
                                                                row)))
                                )),
                new EmptyRow());
    }

    protected Row wrapSectionRow(Section section, Row row) {
        return row;
    }

    @Override
    public Stream<Row> streamBody() {
        return Stream.empty();
    }
}
