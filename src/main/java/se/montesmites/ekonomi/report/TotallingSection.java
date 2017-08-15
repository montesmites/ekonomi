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
    public Stream<TitleRow> streamTitle() {
        return Stream.of(new TitleRow(title));
    }

    @Override
    public Stream<HeaderRow> streamHeader() {
        return Stream.of(new HeaderRow());
    }

    @Override
    public Stream<FooterRow> streamFooter() {
        return Stream.of(
                new FooterRow(
                        () -> sections.stream().flatMap(Section::streamBodyRows)));
    }

    @Override
    public Stream<BodyRow> streamBodyRows() {
        return Stream.empty();
    }
}